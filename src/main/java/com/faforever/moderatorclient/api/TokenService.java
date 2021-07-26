package com.faforever.moderatorclient.api;

import com.faforever.moderatorclient.api.event.HydraAuthorizedEvent;
import com.faforever.moderatorclient.api.event.TokenExpiredEvent;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class TokenService {
  private final ApplicationEventPublisher applicationEventPublisher;
  private RestTemplate restTemplate;
  private EnvironmentProperties environmentProperties;
  private OAuth2AccessToken tokenCache;

  public TokenService(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void prepare(EnvironmentProperties environmentProperties) {
    this.environmentProperties = environmentProperties;
    this.restTemplate = new RestTemplateBuilder().rootUri(environmentProperties.getOauthBaseUrl()).build();
  }

  @SneakyThrows
  public String getRefreshedTokenValue() {
    if (tokenCache == null || tokenCache.isExpired()) {
      log.info("Token expired, requesting new login");
      applicationEventPublisher.publishEvent(new TokenExpiredEvent());
    } else {
      log.debug("Token still valid for {} seconds", tokenCache.getExpiresIn());
    }

    return tokenCache.getValue();
  }

  public void loginWithAuthorizationCode(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON_UTF8));

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("code", code);
    map.add("client_id", environmentProperties.getClientId());
    map.add("redirect_uri", environmentProperties.getOauthRedirectUrl());
    map.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    tokenCache = restTemplate.postForObject(
        "/oauth2/token",
        request,
        OAuth2AccessToken.class
    );

    if (tokenCache != null) {
      applicationEventPublisher.publishEvent(new HydraAuthorizedEvent());
    }
  }
}
