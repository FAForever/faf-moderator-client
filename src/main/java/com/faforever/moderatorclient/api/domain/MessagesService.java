package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Message;
import com.faforever.moderatorclient.api.ElideRouteBuilder;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.domain.events.MessagesChangedEvent;
import com.faforever.moderatorclient.mapstruct.MessagesMapper;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessagesService {
    private final FafApiCommunicationService fafApi;
    private final MessagesMapper messagesMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Inject
    public MessagesService(FafApiCommunicationService fafApi, MessagesMapper messagesMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.fafApi = fafApi;
        this.messagesMapper = messagesMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public CompletableFuture<List<MessageFx>> getAllMessages() {
        return CompletableFuture.supplyAsync(() -> messagesMapper.map(getAllMessagesFromApi()));
    }

    private List<Message> getAllMessagesFromApi() {
        return fafApi.getAll(ElideRouteBuilder.of(Message.class));
    }

    public void updateMessage(MessageFx messageFx) {
        Message message = messagesMapper.map(messageFx);
        fafApi.patch(ElideRouteBuilder.of(Message.class).id(message.getId()), message);
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());
    }

    public void deleteCategory(MessageFx selectedItem) {
        Message message = messagesMapper.map(selectedItem);
        fafApi.delete(ElideRouteBuilder.of(Message.class).id(message.getId()));
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());

    }

    public Message createMessage(MessageFx messageFx) {
        Message message = messagesMapper.map(messageFx);
        Message result = fafApi.post(ElideRouteBuilder.of(Message.class), message);
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());
        return result;
    }
}
