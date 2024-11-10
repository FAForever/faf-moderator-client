package com.faforever.moderatorclient.ui.moderation_reports;

import com.faforever.commons.api.dto.ModerationReportStatus;
import com.faforever.commons.replay.ModeratorEvent;
import com.faforever.commons.replay.ReplayDataParser;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.domain.ModerationReportService;
import com.faforever.moderatorclient.ui.BanInfoController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.PlatformService;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.faforever.moderatorclient.ui.domain.GameFX;
import com.faforever.moderatorclient.ui.domain.ModerationReportFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModerationReportController implements Controller<Region> {
    private final ObjectMapper objectMapper;
    private final ModerationReportService moderationReportService;
    private final UiService uiService;
    private final FafApiCommunicationService communicationService;
    private final PlatformService platformService;
    private final ObservableList<PlayerFX> reportedPlayersOfCurrentlySelectedReport = FXCollections.observableArrayList();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    @Value("${faforever.vault.replay-download-url-format}")
    private String replayDownLoadFormat;

    public Region root;
    public ChoiceBox<ChooseableStatus> statusChoiceBox;
    public TextField playerNameFilterTextField;
    public TableView<ModerationReportFX> reportTableView;
    public Button editReportButton;
    public TableView<PlayerFX> reportedPlayerView;
    public TextArea chatLogTextArea;
    public TextArea moderatorEventTextArea;

    private FilteredList<ModerationReportFX> filteredItemList;
    private ObservableList<ModerationReportFX> itemList;
    private ModerationReportFX currentlySelectedItemNotNull;

    @Override
    public Region getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        statusChoiceBox.setItems(FXCollections.observableArrayList(ChooseableStatus.values()));
        statusChoiceBox.getSelectionModel().select(ChooseableStatus.ALL);

        editReportButton.disableProperty().bind(reportTableView.getSelectionModel().selectedItemProperty().isNull());

        itemList = FXCollections.observableArrayList();
        filteredItemList = new FilteredList<>(itemList);
        renewFilter();
        SortedList<ModerationReportFX> sortedItemList = new SortedList<>(filteredItemList);
        sortedItemList.comparatorProperty().bind(reportTableView.comparatorProperty());
        ViewHelper.buildModerationReportTableView(reportTableView, sortedItemList, this::showChatLog);
        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener(observable -> renewFilter());
        playerNameFilterTextField.textProperty().addListener(observable -> renewFilter());

        reportTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentlySelectedItemNotNull = newValue;
                        reportedPlayersOfCurrentlySelectedReport.setAll(newValue.getReportedUsers());

                        if (newValue.getGame() == null) {
                            chatLogTextArea.setText("not available");
                        } else {
                            chatLogTextArea.setText("not loaded yet");
                        }
                    }
                });

        chatLogTextArea.setText("select a report first");

        ViewHelper.buildUserTableView(platformService, reportedPlayerView, reportedPlayersOfCurrentlySelectedReport, this::addBan,
                playerFX -> ViewHelper.loadForceRenameDialog(uiService, playerFX), false, communicationService);
    }

    private void addBan(PlayerFX accountFX) {
        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
        BanInfoFX ban = new BanInfoFX();
        ban.setPlayer(accountFX);
        banInfoController.setBanInfo(ban);
        banInfoController.addPostedListener(banInfoFX -> onRefreshAllReports());
        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle("Apply new ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoController.preSetReportId(currentlySelectedItemNotNull.getId());
        banInfoDialog.showAndWait();
    }

    private void renewFilter() {
        filteredItemList.setPredicate(moderationReportFx -> {
            String playerFilter = playerNameFilterTextField.getText().toLowerCase();
            if (!Strings.isNullOrEmpty(playerFilter)) {
                boolean reportedPlayerPositive = moderationReportFx.getReportedUsers().stream().anyMatch(accountFX -> accountFX.getLogin().toLowerCase().contains(playerFilter));
                boolean reporterPositive = moderationReportFx.getReporter().getLogin().toLowerCase().contains(playerFilter);
                if (!(reportedPlayerPositive || reporterPositive)) {
                    return false;
                }
            }
            ChooseableStatus selectedItem = statusChoiceBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getModerationReportStatus() != null) {
                ModerationReportStatus moderationReportStatus = selectedItem.getModerationReportStatus();
                return moderationReportFx.getReportStatus() == moderationReportStatus;
            }
            return true;
        });
    }


    public void onRefreshAllReports() {
        moderationReportService.getAllReports().thenAccept(reportFxes -> Platform.runLater(() -> itemList.setAll(reportFxes))).exceptionally(throwable -> {
            log.error("error loading reports", throwable);
            return null;
        });
    }

    public void onEdit() {
        EditModerationReportController editModerationReportController = uiService.loadFxml("ui/edit_moderation_report.fxml");
        editModerationReportController.setModerationReportFx(reportTableView.getSelectionModel().getSelectedItem());
        editModerationReportController.setOnSaveRunnable(() -> Platform.runLater(this::onRefreshAllReports));

        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Edit Report");
        newCategoryDialog.setScene(new Scene(editModerationReportController.getRoot()));
        newCategoryDialog.showAndWait();
    }

    private enum ChooseableStatus {
        ALL(null),
        AWAITING(ModerationReportStatus.AWAITING),
        PROCESSING(ModerationReportStatus.PROCESSING),
        COMPLETED(ModerationReportStatus.COMPLETED),
        DISCARDED(ModerationReportStatus.DISCARDED);

        @Getter
        private final ModerationReportStatus moderationReportStatus;

        ChooseableStatus(ModerationReportStatus moderationReportStatus) {
            this.moderationReportStatus = moderationReportStatus;
        }
    }

    @SneakyThrows
    private void showChatLog(ModerationReportFX report) {
        GameFX game = report.getGame();
        String header = format("CHAT LOG -- Report ID {0} -- Replay ID {1} -- Game \"{2}\"\n\n",
                report.getId(), game.getId(), game.getName());
        Path tempFilePath = Files.createTempFile(format("faf_replay_{0}_", game.getId()), "");

        try {
            String replayUrl = game.getReplayUrl(replayDownLoadFormat);

            log.info("Downloading replay from {} to {}", replayUrl, tempFilePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(replayUrl))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofFile(tempFilePath));

            log.debug("Parsing replay");

            ReplayDataParser replayDataParser = new ReplayDataParser(tempFilePath, objectMapper);
            String chatLog = header + replayDataParser.getChatMessages().stream()
                    .map(message -> {
                        long timeMillis = message.getTime().toMillis();
                        String formattedChatMessageTime = formatChatMessageTime(timeMillis);

                        return format("[{0}] from {1} to {2}: {3}",
                                formattedChatMessageTime,
                                message.getSender(), message.getReceiver(), message.getMessage());
                    })
                    .collect(Collectors.joining("\n"));

            chatLogTextArea.setText(chatLog);

            log.debug("Parsing moderatorEvents");

            List<ModeratorEvent> moderatorEvents = replayDataParser.getModeratorEvents();
            showModeratorEvent(moderatorEvents);
        } catch (Exception e) {
            log.error("Loading replay {} failed", game, e);
            chatLogTextArea.setText(header + format("Loading replay failed due to {0}: \n{1}", e, e.getMessage()));
        }

        Files.delete(tempFilePath);
    }

    private void showModeratorEvent(List<ModeratorEvent> moderatorEvents){
        String moderatorEventsLog = moderatorEvents.stream()
                .map(event -> {
                    long timeMillis = event.time().toMillis();
                    String formattedChatMessageTime = formatChatMessageTime(timeMillis);

                    return format("[{0}] from {1}: {2}",
                            formattedChatMessageTime,
                            event.playerNameFromCommandSource(),
                            event.message());
                })
                .collect(Collectors.joining("\n"));

        moderatorEventTextArea.setText(moderatorEventsLog);
    }

    private String formatChatMessageTime(long timeMillis) {
        if (timeMillis >= 0) {
            return DurationFormatUtils.formatDuration(timeMillis, "HH:mm:ss");
        } else {
            return "N/A"; // replay data contains negative timestamps for whatever reason
        }
    }
}
