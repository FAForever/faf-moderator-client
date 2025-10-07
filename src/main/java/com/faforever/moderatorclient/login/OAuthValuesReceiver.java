package com.faforever.moderatorclient.login;

import com.faforever.moderatorclient.config.EnvironmentProperties;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Opens a minimal HTTP server that retrieves {@literal code} and {@literal state} from the browser. */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthValuesReceiver {

  private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Pattern CODE_PATTERN = Pattern.compile("code=([^ &]+)");
  private static final Pattern STATE_PATTERN = Pattern.compile("state=([^ &]+)");
  private static final Pattern ERROR_PATTERN = Pattern.compile("error=([^ &]+)");
  private static final Pattern ERROR_SCOPE_DENIED = Pattern.compile("scope_denied");
  private static final Pattern ERROR_NO_CSRF = Pattern.compile("No\\+CSRF\\+value");

  private EnvironmentProperties environmentProperties;

  private CountDownLatch redirectUriLatch;
  private URI redirectUri;
  private String state;
  private String codeVerifier;
  private CompletableFuture<Values> valuesFuture;

  public CompletableFuture<Values> receiveValues(EnvironmentProperties environmentProperties) {
    if (valuesFuture != null && !valuesFuture.isDone()) {
      openBrowserToLogin();
      return valuesFuture;
    }
    this.environmentProperties = environmentProperties;
    this.state = RandomStringUtils.secureStrong().nextAlphanumeric(64, 128);
    this.codeVerifier = RandomStringUtils.secureStrong().nextAlphanumeric(64, 128);

    redirectUriLatch = new CountDownLatch(1);
    valuesFuture = CompletableFuture.supplyAsync(this::readValues);
    return valuesFuture;

  }

  private void openBrowserToLogin() {
    if (redirectUriLatch == null) {
      throw new IllegalStateException("Redirect socket is not open");
    }

    CompletableFuture.runAsync(() -> {
      try {
        redirectUriLatch.await();
      } catch (InterruptedException ignored) {}
      openHydraUrl();
    });
  }

  private String getHydraUrl() {
    String codeChallenge = BASE64_ENCODER.encodeToString(Hashing.sha256()
            .hashString(codeVerifier, StandardCharsets.US_ASCII)
            .asBytes());
    return String.format("%s/oauth2/auth?response_type=code&client_id=%s&state=%s&redirect_uri=%s&scope=%s&code_challenge_method=S256&code_challenge=%s", environmentProperties.getOauthBaseUrl(), environmentProperties.getClientId(), state, redirectUri.toASCIIString(), environmentProperties.getOauthScopes(), codeChallenge);
  }

  private Values readValues() {
    try (ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
      redirectUri = UriComponentsBuilder.fromUriString("http://127.0.0.1")
                                        .port(serverSocket.getLocalPort())
                                        .build()
                                        .toUri();
      redirectUriLatch.countDown();

      openHydraUrl();

      Socket socket = serverSocket.accept();
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      String request = reader.readLine();
      log.info(request);

      boolean success = false;

      // Do not try with resources as the socket needs to stay open.
      try {
        checkForError(request);
        Values values = readValues(request);
        if (!state.equals(values.state)) {
          throw new IllegalStateException("State returned by the server does not match expected state");
        }
        success = true;
        return values;
      } finally {
        writeResponse(socket, success);
        reader.close();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Could not get code", e);
    } finally {
      redirectUriLatch = null;
    }
  }

  private void openHydraUrl() {
    showDocument(getHydraUrl());
  }

  /**
   * Opens the specified URI in a new browser window or tab. Note: The code is copied from
   * {@link com.sun.javafx.application.HostServicesDelegate#showDocument(String)} The only fix is that all any
   * exceptions are intercepted by our side, and we can tell the user what happened wrong.
   */
  public void showDocument(String url) {
    final String[] browsers = {"xdg-open", "google-chrome", "firefox", "opera", "konqueror", "mozilla"};

    String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Mac OS")) {
        Runtime.getRuntime().exec(new String[]{"open", url});
      } else if (osName.startsWith("Windows")) {
        Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
      } else { //assume Unix or Linux
        String browser = null;
        for (String b : browsers) {
          if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
            Runtime.getRuntime().exec(new String[]{browser = b, url});
          }
        }
        if (browser == null) {
          throw new Exception("No web browser found");
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private void writeResponse(Socket socket, boolean success) throws IOException {
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

      String html;

      if (success) {
        try (InputStream inputStream = OAuthValuesReceiver.class.getResourceAsStream("/login_success.html")) {
          html = new String(inputStream.readAllBytes());
        }
      } else {
        try (InputStream inputStream = OAuthValuesReceiver.class.getResourceAsStream("/login_failed.html")) {
          html = new String(inputStream.readAllBytes());
        }
      }

      writer
          .append("HTTP/1.1 200 OK\r\n")
          .append("Content-Length ")
          .append(String.valueOf(html.length()))
          .append("\r\n")
          .append("Content-Type: text/html\r\n")
          .append("Connection: Closed\r\n")
          .append("\r\n")
          .append(html);
    }
  }

  private Values readValues(String request) {
    String code = extractValue(request, CODE_PATTERN);
    String state = extractValue(request, STATE_PATTERN);
    return new Values(code, codeVerifier, state, redirectUri, environmentProperties.getClientId());
  }

  private String formatRequest(String request) {
    return URLDecoder.decode(request, StandardCharsets.UTF_8);
  }

  private String extractValue(String request, Pattern pattern) {
    Matcher matcher = pattern.matcher(request);
    if (!matcher.find()) {
      throw new IllegalStateException("Could not extract value with pattern '" + pattern + "' from: " + formatRequest(request));
    }
    return matcher.group(1);
  }

  private void checkForError(String request) {
    Matcher matcher = ERROR_PATTERN.matcher(request);
    if (matcher.find()) {
      String errorMessage = "Login failed with error '" + matcher.group(1) + "'. The full request is: " + formatRequest(request);
      if (ERROR_SCOPE_DENIED.matcher(request).find()) {
        throw new KnownLoginErrorException(errorMessage, "login.scopeDenied");
      }

      if (ERROR_NO_CSRF.matcher(request).find()) {
        throw new KnownLoginErrorException(errorMessage, "login.noCSRF");
      }
      throw new IllegalStateException(errorMessage);
    }
  }

  public record Values(String code, String codeVerifier, String state, URI redirectUri, String clientId) {}
}
