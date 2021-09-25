package com.faforever.moderatorclient.api.domain;


import com.faforever.commons.api.dto.GroupPermission;
import com.faforever.commons.api.dto.UserGroup;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.GroupPermissionMapper;
import com.faforever.moderatorclient.mapstruct.UserGroupMapper;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.faforever.commons.api.elide.ElideNavigator.qBuilder;

@Service
@Slf4j
public class PermissionService {

	private final GroupPermissionMapper groupPermissionMapper;
	private final UserGroupMapper userGroupMapper;
	private final FafApiCommunicationService fafApi;

	public PermissionService(GroupPermissionMapper groupPermissionMapper, UserGroupMapper userGroupMapper, FafApiCommunicationService fafApi) {
		this.groupPermissionMapper = groupPermissionMapper;
		this.userGroupMapper = userGroupMapper;
		this.fafApi = fafApi;
	}

	public void deleteUserGroup(UserGroupFX userGroupFX) {
		deleteUserGroup(userGroupMapper.map(userGroupFX));
	}

	public void deleteUserGroup(UserGroup userGroup) {
		log.debug("Deleting userGroup id: {}", userGroup.getId());
		fafApi.delete(ElideNavigator.of(userGroup));
	}

	public void patchUserGroup(UserGroupFX userGroupFX) {
		patchUserGroup(userGroupMapper.map(userGroupFX));
	}

	public void patchUserGroup(UserGroup userGroup) {
		log.debug("Updating userGroup id: {}", userGroup.getId());
		fafApi.patch(ElideNavigator.of(userGroup),
				new UserGroup()
						.setMembers(userGroup.getMembers())
						.setPermissions(userGroup.getPermissions())
						.setPublic_(userGroup.isPublic_())
						.setId(userGroup.getId()));
	}

	public UserGroupFX postUserGroup(UserGroupFX userGroupFX) {
		return postUserGroup(userGroupMapper.map(userGroupFX));
	}

	public UserGroupFX postUserGroup(UserGroup userGroup) {
		log.debug("Creating userGroup: {}", userGroup.getTechnicalName());
		return userGroupMapper.map(fafApi.post(ElideNavigator.of(UserGroup.class).collection(), userGroup));
	}

	public CompletableFuture<List<GroupPermissionFX>> getAllGroupPermissions() {
		return CompletableFuture.supplyAsync(() -> {
            List<GroupPermission> groupPermissions = fafApi.getAll(GroupPermission.class, ElideNavigator.of(GroupPermission.class)
					.collection()
					.addInclude("userGroups")
			);
			return groupPermissions.stream().map(groupPermissionMapper::map).collect(Collectors.toList());
		});
	}

	public CompletableFuture<List<UserGroupFX>> getAllUserGroups() {
		return CompletableFuture.supplyAsync(() -> {
			List<UserGroup> userGroups = fafApi.getAll(UserGroup.class, ElideNavigator.of(UserGroup.class)
					.collection()
					.addInclude("children")
					.addInclude("parent")
					.addInclude("members")
					.addInclude("permissions")
			);
			return userGroups.stream().map(userGroupMapper::map).collect(Collectors.toList());
		});
	}

	public CompletableFuture<List<UserGroupFX>> getPlayersUserGroups(PlayerFX player) {
		return CompletableFuture.supplyAsync(() -> {
			List<UserGroup> userGroups = fafApi.getAll(UserGroup.class, ElideNavigator.of(UserGroup.class)
					.collection()
					.addInclude("children")
					.addInclude("parent")
					.addInclude("members")
					.addInclude("permissions")
					.setFilter(qBuilder().string("members.id").eq(player.getId()))
			);
			return userGroups.stream().map(userGroupMapper::map).collect(Collectors.toList());
		});
	}
}

