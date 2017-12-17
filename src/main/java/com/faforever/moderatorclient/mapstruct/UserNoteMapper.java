package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.UserNote;
import com.faforever.moderatorclient.ui.domain.UserNoteFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, PlayerMapper.class})
public interface UserNoteMapper {
    UserNoteFX map(UserNote userNote);

    UserNote map(UserNoteFX userNoteFX);

    List<UserNoteFX> map(List<UserNote> userNoteList);
}
