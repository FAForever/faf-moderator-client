package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.User;
import com.faforever.moderatorclient.ui.domain.UserFX;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, NameRecordMapper.class, BanInfoMapper.class, AvatarAssignmentMapper.class, ModerationReportMapper.class, CycleAvoidingMappingContext.class})
public abstract class UserMapper {
    public abstract UserFX map(User dto);

    public abstract User map(UserFX fxBean);

    public abstract List<UserFX> mapToFx(List<User> dtoList);

    public abstract List<User> mapToDto(List<UserFX> fxBeanList);

    public abstract Set<UserFX> mapToFx(Set<User> dtoList);

    public abstract Set<User> mapToDto(Set<UserFX> fxBeanList);
}
