package com.faforever.moderatorclient.api;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.dto.AvatarMetadata;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class AvatarApiClient {
    private final FafApiCommunicationService fafApiCommunicationService;

    @Autowired
    public AvatarApiClient(FafApiCommunicationService fafApiCommunicationService) {
        this.fafApiCommunicationService = fafApiCommunicationService;
    }

    public void uploadAvatar(String name, File avatarImageFile) {
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = createAvatarMultipartRequest(name, avatarImageFile);
        final String route = "/avatars/upload";
        log.debug("Sending API request: {}", route);
        fafApiCommunicationService.getRestOperations().exchange(
                route,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }

    public void reuploadAvatar(String avatarId, String name, File avatarImageFile) {
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = createAvatarMultipartRequest(name, avatarImageFile);
        final String route = "/avatars/{0}/upload";
        log.debug("Sending API request: {}", route);
        fafApiCommunicationService.getRestOperations().exchange(
                route,
                HttpMethod.POST,
                requestEntity,
                String.class,
                avatarId
        );
    }

    public void deleteAvatar(String avatarId) {
        final String route = "/avatars/{0}";
        log.debug("Sending API request: {}", route);
        fafApiCommunicationService.getRestOperations().delete(route, avatarId);
    }

    public List<Avatar> getAllAvatarsWithPlayerAssignments() {
        return fafApiCommunicationService.getAll(ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player"));
    }

    public List<Avatar> findAvatarsById(@NotNull String pattern) {
        return findAvatarsByAttribute("id", pattern);
    }

    public List<Avatar> findAvatarsByTooltip(@NotNull String pattern) {
        return findAvatarsByAttribute("tooltip", pattern);
    }

    public List<Avatar> findAvatarsByAssignedUser(@NotNull String pattern) {
        ElideRouteBuilder<Avatar> routeBuilder = ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player")
                .filter(ElideRouteBuilder.qBuilder().string("assignments.player.id").eq(pattern)
                        .or().string("assignments.player.login").eq(pattern));

        return fafApiCommunicationService.getAll(routeBuilder);
    }

    public void updateAvatarMetadata(String avatarId, String name) {
        fafApiCommunicationService.patch(ElideRouteBuilder.of(Avatar.class).id(avatarId),
                (Avatar) new Avatar().setTooltip(name).setId(avatarId));
    }

    @NotNull
    private HttpEntity<LinkedMultiValueMap<String, Object>> createAvatarMultipartRequest(String name, File avatarImageFile) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new FileSystemResource(avatarImageFile));
        map.add("metadata", new AvatarMetadata().setName(name));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(map, headers);
    }

    private List<Avatar> findAvatarsByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for avatars by attribute '{}' with pattern: {}", attribute, pattern);
        ElideRouteBuilder<Avatar> routeBuilder = ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player")
                .filter(ElideRouteBuilder.qBuilder().string(attribute).eq(pattern));

        List<Avatar> result = fafApiCommunicationService.getAll(routeBuilder);
        log.trace("found {} avatars", result.size());
        return result;
    }
}
