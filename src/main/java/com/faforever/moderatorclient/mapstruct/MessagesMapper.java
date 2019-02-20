package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Message;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {JavaFXMapper.class})
public abstract class MessagesMapper {
    public abstract MessageFx map(Message dto);

    public abstract Message map(MessageFx fxBean);

    public abstract List<MessageFx> map(List<Message> dtoList);
}
