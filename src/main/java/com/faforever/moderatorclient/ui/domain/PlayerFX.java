package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.BanLevel;
import com.faforever.commons.api.dto.BanStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.time.OffsetDateTime;

public class PlayerFX extends AbstractEntityFX {
    private final StringProperty login;
    private final StringProperty email;
    private final StringProperty userAgent;
    private final StringProperty steamId;
    private final StringProperty recentIpAddress;
    private final StringProperty representation;
    private final ObjectProperty<OffsetDateTime> lastLogin;
    private final ObservableSet<UniqueIdFx> uniqueIds;
    private final ObservableList<NameRecordFX> names;
    private final ObservableList<BanInfoFX> bans;
    private final ObservableList<AvatarAssignmentFX> avatarAssignments;

    public PlayerFX() {
        login = new SimpleStringProperty();
        email = new SimpleStringProperty();
        userAgent = new SimpleStringProperty();
        steamId = new SimpleStringProperty();
        recentIpAddress = new SimpleStringProperty();
        lastLogin = new SimpleObjectProperty<>();

        representation = new SimpleStringProperty();
        representation.bind(Bindings.concat(login, " [id ", idProperty(), "]"));

        names = FXCollections.observableArrayList();
        bans = FXCollections.observableArrayList();
        avatarAssignments = FXCollections.observableArrayList();
        uniqueIds = FXCollections.observableSet();
    }


    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public StringProperty loginProperty() {
        return login;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getUserAgent() {
        return userAgent.get();
    }

    public void setUserAgent(String userAgent) {
        this.userAgent.set(userAgent);
    }

    public StringProperty userAgentProperty() {
        return userAgent;
    }

    public String getSteamId() {
        return steamId.get();
    }

    public void setSteamId(String steamId) {
        this.steamId.set(steamId);
    }

    public StringProperty steamIdProperty() {
        return steamId;
    }

    public String getRecentIpAddress() {
        return recentIpAddress.get();
    }

    public void setRecentIpAddress(String recentIpAddress) {
        this.recentIpAddress.set(recentIpAddress);
    }

    public StringProperty recentIpAddressProperty() {
        return recentIpAddress;
    }

    public String getRepresentation() {
        return representation.get();
    }

    public StringProperty representationProperty() {
        return representation;
    }

    public ObservableList<NameRecordFX> getNames() {
        return names;
    }

    public void setNames(ObservableList<NameRecordFX> nameRecordFXObservableList) {
        names.clear();
        if (nameRecordFXObservableList != null) {
            names.addAll(nameRecordFXObservableList);
        }
    }

    public ObservableSet<UniqueIdFx> getUniqueIds() {
        return uniqueIds;
    }

    public void setUniqueIds(ObservableSet<UniqueIdFx> uniqueIdFxObservableList) {
        uniqueIds.clear();

        if (uniqueIdFxObservableList != null) {
            uniqueIds.addAll(uniqueIdFxObservableList);
        }
    }

    public ObservableList<BanInfoFX> getBans() {
        return bans;
    }

    public void setBans(ObservableList<BanInfoFX> banInfoFXObservableList) {
        bans.clear();

        if (banInfoFXObservableList != null) {
            bans.addAll(banInfoFXObservableList);
        }
    }

    public ObservableList<AvatarAssignmentFX> getAvatarAssignments() {
        return avatarAssignments;
    }

    public void setAvatarAssignments(ObservableList<AvatarAssignmentFX> avatarAssignmentFXObservableList) {
        avatarAssignments.clear();
        if (avatarAssignmentFXObservableList != null) {
            avatarAssignments.addAll(avatarAssignmentFXObservableList);
        }
    }

    public boolean isBannedGlobally() {
        return !bans.filtered(banInfoFX -> banInfoFX.getBanStatus() == BanStatus.BANNED && banInfoFX.getLevel() == BanLevel.GLOBAL).isEmpty();
    }

    public OffsetDateTime getLastLogin() {
        return lastLogin.get();
    }

    public void setLastLogin(OffsetDateTime lastLogin) {
        this.lastLogin.set(lastLogin);
    }

    public ObjectProperty<OffsetDateTime> lastLoginProperty() {
        return lastLogin;
    }
}
