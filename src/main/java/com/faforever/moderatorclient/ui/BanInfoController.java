package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.BanDurationType;
import com.faforever.commons.api.dto.BanInfo;
import com.faforever.commons.api.dto.BanLevel;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.domain.BanService;
import com.faforever.moderatorclient.mapstruct.PlayerMapper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.faforever.moderatorclient.ui.domain.ModerationReportFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class BanInfoController implements Controller<Pane> {
    private final FafApiCommunicationService fafApi;
    private final BanService banService;
    private final PlayerMapper playerMapper;

    public GridPane root;
    public TextField affectedUserTextField;
    public TextField banAuthorTextField;
    public TextField banReasonTextField;
    public TextField revocationReasonTextField;
    public TextField revocationAuthorTextField;
    public TextField banDaysTextField;
    public TextField untilTextField;
    public Label untilDateTimeValidateLabel;
    public RadioButton permanentBanRadioButton;
    public RadioButton forNoOfDaysBanRadioButton;
    public RadioButton temporaryBanRadioButton;
    public RadioButton chatOnlyBanRadioButton;
    public RadioButton vaultBanRadioButton;
    public RadioButton globalBanRadioButton;
    public Button revokeButton;
    public Label userLabel;
    public Label banIsRevokedNotice;
    public TextField revocationTimeTextField;
    public VBox revokeOptions;
    public TextField reportIdTextField;

    @Getter
    private BanInfoFX banInfo;
    private Consumer<BanInfoFX> postedListener;
    private Runnable onBanRevoked;

    public void addRevokedListener(Runnable listener) {
        this.onBanRevoked = listener;
    }

    public void addPostedListener(Consumer<BanInfoFX> listener) {
        this.postedListener = listener;
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        banIsRevokedNotice.managedProperty().bind(banIsRevokedNotice.visibleProperty());
    }

    public void onRevokeTimeTextChanged() {
        revocationTimeTextField.setStyle("-fx-text-fill: green");
        try {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(revocationTimeTextField.getText());
        } catch (Exception e) {
            revocationTimeTextField.setStyle("-fx-text-fill: red");
        }
    }

    public void setBanInfo(BanInfoFX banInfo) {
        this.banInfo = banInfo;

        if (banInfo.getId() != null) {
            revokeOptions.setDisable(false);

            affectedUserTextField.setText(banInfo.getPlayer().representationProperty().get());
            Optional.ofNullable(banInfo.getAuthor()).ifPresent(author -> banAuthorTextField.setText(author.representationProperty().get()));
            banReasonTextField.setText(banInfo.getReason());

            revocationReasonTextField.setDisable(false);
            revokeButton.setDisable(false);

            permanentBanRadioButton.setSelected(banInfo.getDuration() == BanDurationType.PERMANENT);
            temporaryBanRadioButton.setSelected(banInfo.getDuration() == BanDurationType.TEMPORARY);
            Optional.ofNullable(banInfo.getExpiresAt()).ifPresent(offsetDateTime -> untilTextField.setText(offsetDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

            if (banInfo.getRevokeTime() != null) {
                banIsRevokedNotice.setVisible(true);
                revocationReasonTextField.setText(banInfo.getRevokeReason());
                revocationTimeTextField.setText(banInfo.getRevokeTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                revocationAuthorTextField.setText(banInfo.getRevokeAuthor() == null ? "" : banInfo.getRevokeAuthor().getLogin());
            } else {
                revocationTimeTextField.setText(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            chatOnlyBanRadioButton.setSelected(banInfo.getLevel() == BanLevel.CHAT);
            vaultBanRadioButton.setSelected(banInfo.getLevel() == BanLevel.VAULT);
            globalBanRadioButton.setSelected(banInfo.getLevel() == BanLevel.GLOBAL);

            ModerationReportFX moderationReportFx = banInfo.getModerationReport();
            if (moderationReportFx != null) {
                reportIdTextField.setText(moderationReportFx.getId());
            }

        } else {

            PlayerFX player = banInfo.getPlayer();
            if (player != null) {
                affectedUserTextField.setText(player.representationProperty().get());
            } else {
                affectedUserTextField.setEditable(true);
                affectedUserTextField.setDisable(false);
                userLabel.setText("Affected User ID");
            }
        }
    }

    public void onSave() {
        Assert.notNull(banInfo, "You can't save if banInfo is null.");

        if (!validate()) {
            return;
        }
        if (banInfo.getPlayer() == null) {
            PlayerFX playerFX = new PlayerFX();
            playerFX.setId(affectedUserTextField.getText());
            banInfo.setPlayer(playerFX);
        }

        banInfo.setReason(banReasonTextField.getText());

        if (forNoOfDaysBanRadioButton.isSelected())
            banInfo.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(Long.parseLong(banDaysTextField.getText())));
        else if (temporaryBanRadioButton.isSelected())
            banInfo.setExpiresAt(OffsetDateTime.of(LocalDateTime.parse(untilTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC));
        else {
            banInfo.setExpiresAt(null);
        }

        if (chatOnlyBanRadioButton.isSelected()) {
            banInfo.setLevel(BanLevel.CHAT);
        } else if (vaultBanRadioButton.isSelected()) {
            banInfo.setLevel(BanLevel.VAULT);
        } else {
            banInfo.setLevel(BanLevel.GLOBAL);
        }

        if (!StringUtils.isBlank(reportIdTextField.getText())) {
            ModerationReportFX moderationReportFx = new ModerationReportFX();
            moderationReportFx.setId(reportIdTextField.getText());
            banInfo.setModerationReport(moderationReportFx);
        }


        if (banInfo.getId() == null) {
            log.debug("Creating ban for player '{}' with reason: {}", banInfo.getPlayer().toString(), banReasonTextField.getText());
            String newBanId = banService.createBan(banInfo);
            BanInfoFX loadedBanInfo = banService.getBanInfoById(newBanId);
            if (postedListener != null) {
                postedListener.accept(loadedBanInfo);
            }
        } else {
            log.debug("Updating ban id '{}'", banInfo.getId());
            banService.patchBanInfo(banInfo);
        }
        close();
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (banInfo.getPlayer() == null) {
            try {
                Integer.parseInt(affectedUserTextField.getText());
            } catch (Exception e) {
                validationErrors.add("You must specify an affected user");
            }
        }

        if (StringUtils.isBlank(banReasonTextField.getText())) {
            validationErrors.add("No ban reason is given.");
        }

        if (!forNoOfDaysBanRadioButton.isSelected() && !temporaryBanRadioButton.isSelected() && !permanentBanRadioButton.isSelected()) {
            validationErrors.add("No ban duration is selected.");
        }

        if (!chatOnlyBanRadioButton.isSelected() &&
                !vaultBanRadioButton.isSelected() &&
                !globalBanRadioButton.isSelected()) {
            validationErrors.add("No ban type is selected.");
        }

        if (!StringUtils.isBlank(reportIdTextField.getText())) {
            try {
                Integer.parseInt(reportIdTextField.getText());
            } catch (Exception e) {
                validationErrors.add("Report ID must be a number.");
            }
        }

        if (forNoOfDaysBanRadioButton.isSelected()) {
            try {
                Long.parseUnsignedLong(banDaysTextField.getText());
            } catch (NumberFormatException e) {
                validationErrors.add("Invalid number of days.");
            }
        }

        if (temporaryBanRadioButton.isSelected()) {
            try {
                LocalDateTime.parse(untilTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                validationErrors.add("Expiration date of ban is invalid.");
            }
        }

        if (validationErrors.size() > 0) {
            ViewHelper.errorDialog("Validation failed",
                    String.join("\n", validationErrors)
            );

            return false;
        }

        return true;
    }

    public void onRevoke() {
        Assert.notNull(banInfo, "You can't revoke if banInfo is null.");
        List<String> errors = new ArrayList<>();

        String revocationReason = revocationReasonTextField.getText();

        if (StringUtils.isBlank(revocationReason)) {
            errors.add("The reason of revocation must not be empty.");
        }
        OffsetDateTime revokeTime = null;
        try {
            revokeTime = OffsetDateTime.of(LocalDateTime.parse(revocationTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            log.debug("Revoke time invalid", e);
            errors.add("Invalid date for revocation.");
        }

        if (!errors.isEmpty()) {
            ViewHelper.errorDialog("Could not revoke",
                    String.join("\n", errors));
            return;
        }

        log.debug("Revoking ban id '{}' with reason: {}", banInfo.getId(), revocationReason);

        PlayerFX author = new PlayerFX();
        author.setId(fafApi.getMeResult().getId());
        banInfo.setRevokeAuthor(author);
        banInfo.setRevokeReason(revocationReason);
        banInfo.setRevokeTime(revokeTime);
        banInfo.setUpdateTime(OffsetDateTime.now());

        BanInfo banInfoUpdate = new BanInfo();
        banInfoUpdate.setId(banInfo.getId());
        banInfoUpdate.setRevokeReason(revocationReason);
        banInfoUpdate.setRevokeTime(revokeTime);

        banService.updateBan(banInfoUpdate);
        if (onBanRevoked != null) {
            onBanRevoked.run();
        }
        close();
    }

    public void onAbort() {
        close();
    }

    public void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void onDurationTextChange() {
        if (untilTextField.getText().length() == 0) {
            untilDateTimeValidateLabel.setText("");
            return;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(untilTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            untilDateTimeValidateLabel.setText("valid");
            untilDateTimeValidateLabel.setStyle("-fx-text-fill: green");
        } catch (DateTimeParseException e) {
            untilDateTimeValidateLabel.setText("invalid");
            untilDateTimeValidateLabel.setStyle("-fx-text-fill: red");
        }
    }

    public void preSetReportId(String id) {
        reportIdTextField.setText(id);
        reportIdTextField.setDisable(true);
    }

}
