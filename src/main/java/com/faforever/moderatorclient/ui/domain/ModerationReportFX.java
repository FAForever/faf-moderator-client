package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.ModerationReportStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class ModerationReportFX extends AbstractEntityFX {
	private final StringProperty reportDescription;
	private final ObjectProperty<ModerationReportStatus> reportStatus;
	private final StringProperty gameIncidentTimecode;
	private final StringProperty moderatorNotice;
	private final StringProperty moderatorPrivateNote;
	private final ObservableSet<BanInfoFX> bans;
	private final ObjectProperty<UserFX> reporter;
	private final ObjectProperty<GameFX> game;
	private final ObjectProperty<UserFX> lastModerator;
	private final ObservableSet<UserFX> reportedUsers;

	public ModerationReportFX() {
		reportDescription = new SimpleStringProperty();
		reportStatus = new SimpleObjectProperty<>();
		gameIncidentTimecode = new SimpleStringProperty();
		moderatorNotice = new SimpleStringProperty();
		moderatorPrivateNote = new SimpleStringProperty();
		bans = FXCollections.observableSet();
		reporter = new SimpleObjectProperty<>();
		game = new SimpleObjectProperty<>();
		lastModerator = new SimpleObjectProperty<>();
		reportedUsers = FXCollections.observableSet();
	}

	public String getReportDescription() {
		return reportDescription.get();
	}

	public void setReportDescription(String reportDescription) {
		this.reportDescription.set(reportDescription);
	}

	public StringProperty reportDescriptionProperty() {
		return reportDescription;
	}

	public ModerationReportStatus getReportStatus() {
		return reportStatus.get();
	}

	public void setReportStatus(ModerationReportStatus reportStatus) {
		this.reportStatus.set(reportStatus);
	}

	public ObservableObjectValue<ModerationReportStatus> reportStatusProperty() {
		return reportStatus;
	}

	public String getGameIncidentTimecode() {
		return gameIncidentTimecode.get();
	}

	public void setGameIncidentTimecode(String gameIncidentTimecode) {
		this.gameIncidentTimecode.set(gameIncidentTimecode);
	}

	public StringProperty gameIncidentTimecodeProperty() {
		return gameIncidentTimecode;
	}

	public String getModeratorNotice() {
		return moderatorNotice.get();
	}

	public void setModeratorNotice(String moderatorNotice) {
		this.moderatorNotice.set(moderatorNotice);
	}

	public StringProperty moderatorNoticeProperty() {
		return moderatorNotice;
	}

	public String getModeratorPrivateNote() {
		return moderatorPrivateNote.get();
	}

	public void setModeratorPrivateNote(String moderatorPrivateNote) {
		this.moderatorPrivateNote.set(moderatorPrivateNote);
	}

	public StringProperty moderatorPrivateNoteProperty() {
		return moderatorPrivateNote;
	}

	public ObservableSet<BanInfoFX> getBans() {
		return bans;
	}

	public UserFX getReporter() {
		return reporter.get();
	}

	public void setReporter(UserFX reporter) {
		this.reporter.set(reporter);
	}

	public ObservableObjectValue<UserFX> reporterProperty() {
		return reporter;
	}

	public GameFX getGame() {
		return game.get();
	}

	public void setGame(GameFX game) {
		this.game.set(game);
	}

	public ObservableObjectValue<GameFX> gameProperty() {
		return game;
	}

	public UserFX getLastModerator() {
		return lastModerator.get();
	}

	public void setLastModerator(UserFX lastModerator) {
		this.lastModerator.set(lastModerator);
	}

	public ObservableObjectValue<UserFX> lastModeratorProperty() {
		return lastModerator;
	}

	public ObservableSet<UserFX> getReportedUsers() {
		return reportedUsers;
	}
}
