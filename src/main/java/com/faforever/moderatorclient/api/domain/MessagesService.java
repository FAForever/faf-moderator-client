package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Message;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.domain.events.MessagesChangedEvent;
import com.faforever.moderatorclient.mapstruct.MessagesMapper;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessagesService {
    private final FafApiCommunicationService fafApi;
    private final MessagesMapper messagesMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CompletableFuture<List<MessageFx>> getAllMessages() {
        return CompletableFuture.supplyAsync(() -> messagesMapper.map(getAllMessagesFromApi()));
    }

    private List<Message> getAllMessagesFromApi() {
        return fafApi.getAll(Message.class, ElideNavigator.of(Message.class).collection());
    }

    public void updateMessage(MessageFx messageFx) {
        Message message = messagesMapper.map(messageFx);
        fafApi.patch(ElideNavigator.of(Message.class).id(message.getId()), message);
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());
    }

    public void deleteMessage(MessageFx selectedItem) {
        Message message = messagesMapper.map(selectedItem);
        fafApi.delete(ElideNavigator.of(Message.class).id(message.getId()));
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());

    }

    public Message createMessage(MessageFx messageFx) {
        Message message = messagesMapper.map(messageFx);
        Message result = fafApi.post(ElideNavigator.of(Message.class).collection(), message);
        applicationEventPublisher.publishEvent(new MessagesChangedEvent());
        return result;
    }

    public Message putMessage(MessageFx messageFx) {
        Message message = messagesMapper.map(messageFx);
        List<Message> messages = fafApi.getAll(Message.class, ElideNavigator.of(Message.class)
                .collection()
                .setFilter(ElideNavigator.qBuilder().string("region").eq(message.getRegion())
                        .and().string("language").eq(message.getLanguage())
                        .and().string("key").eq(message.getKey())));
        if (messages.size() > 1) {
            throw new IllegalStateException("message should be unique by region, language and key");
        }
        if (!messages.isEmpty()) {
            log.info("deleting existing message ''{}''", message);
            messages.forEach(messageFound -> deleteMessage(messagesMapper.map(messageFound)));
        }
        return createMessage(messageFx);
    }

}
