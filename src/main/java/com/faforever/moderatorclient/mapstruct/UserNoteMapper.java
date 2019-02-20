package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.UserNote;
import com.faforever.moderatorclient.ui.domain.UserNoteFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class})
public interface UserNoteMapper {
    UserNoteFX map(UserNote dto);

    UserNote map(UserNoteFX fxBean);

    List<UserNoteFX> map(List<UserNote> dtoList);
}
