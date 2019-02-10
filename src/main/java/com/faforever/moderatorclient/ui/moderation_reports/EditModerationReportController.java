package com.faforever.moderatorclient.ui.moderation_reports;

import com.faforever.commons.api.dto.ModerationReport;
import com.faforever.commons.api.dto.ModerationReportStatus;
import com.faforever.moderatorclient.api.domain.ModerationReportService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.domain.ModerationReportFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class EditModerationReportController implements Controller<Pane> {
	private final ModerationReportService moderationReportService;

	public TextArea privateNoteTextArea;
	public TextArea publicNoteTextArea;
	public ChoiceBox<ModerationReportStatus> statusChoiceBox;
	public Pane root;

	private Runnable onSaveRunnable;
	private ModerationReportFX moderationReportFx;

	public EditModerationReportController(ModerationReportService moderationReportService) {
		this.moderationReportService = moderationReportService;
	}

	@FXML
	public void initialize() {
		statusChoiceBox.setItems(FXCollections.observableArrayList(ModerationReportStatus.values()));
	}

	public void onSave() {
		ModerationReport updateModerationReport = new ModerationReport();
		updateModerationReport.setId(moderationReportFx.getId());
		updateModerationReport.setReportStatus(statusChoiceBox.getSelectionModel().getSelectedItem());
		updateModerationReport.setModeratorPrivateNote(privateNoteTextArea.getText());
		updateModerationReport.setModeratorNotice(publicNoteTextArea.getText());

		moderationReportService.patchReport(updateModerationReport);

		moderationReportFx.setReportStatus(statusChoiceBox.getSelectionModel().getSelectedItem());
		moderationReportFx.setModeratorPrivateNote(privateNoteTextArea.getText());
		moderationReportFx.setModeratorNotice(publicNoteTextArea.getText());

		onSaveRunnable.run();
		close();
	}

	public void setOnSaveRunnable(Runnable onSaveRunnable) {
		this.onSaveRunnable = onSaveRunnable;
	}

	public void setModerationReportFx(ModerationReportFX moderationReportFx) {
		this.moderationReportFx = moderationReportFx;
		statusChoiceBox.getSelectionModel().select(moderationReportFx.getReportStatus());
		privateNoteTextArea.setText(moderationReportFx.getModeratorPrivateNote());
		publicNoteTextArea.setText(moderationReportFx.getModeratorNotice());
	}

	@Override
	public Pane getRoot() {
		return root;
	}

	public void close() {
		Stage stage = (Stage) root.getScene().getWindow();
		stage.close();
	}
}
