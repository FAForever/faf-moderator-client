package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.AvatarApiClient;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class AvatarService {
    private final AvatarApiClient avatarApiClient;

    public AvatarService(AvatarApiClient avatarApiClient) {
        this.avatarApiClient = avatarApiClient;
    }

    public List<Avatar> getAll() {
        log.debug("Retrieving all avatars");
        List<Avatar> result = avatarApiClient.getAllAvatarsWithPlayerAssignments();
        log.trace("found {} avatars", result.size());
        return result;
    }

    public List<Avatar> findAvatarsById(@NotNull String pattern) {
        return avatarApiClient.findAvatarsById(pattern);
    }

    public List<Avatar> findAvatarsByTooltip(@NotNull String pattern) {
        return avatarApiClient.findAvatarsByTooltip(pattern);
    }

    public List<Avatar> findAvatarsByAssignedUser(@NotNull String pattern) {
        log.debug("Searching for avatars by assigned player with pattern: {}", pattern);
        List<Avatar> result = avatarApiClient.findAvatarsByAssignedUser(pattern);
        log.trace("found {} avatars", result.size());
        return result;
    }

    public void createAvatar(String name, File avatarImageFile) {
        avatarApiClient.uploadAvatar(name, avatarImageFile);
    }

    public void updateAvatar(String avatarId, String name, File avatarImageFile) {
        if (avatarImageFile != null) {
            avatarApiClient.reuploadAvatar(avatarId, name, avatarImageFile);
        } else {
            avatarApiClient.updateAvatarMetadata(avatarId, name);
        }

    }

    public void deleteAvatar(String avatarId) {
        avatarApiClient.deleteAvatar(avatarId);
    }
}
