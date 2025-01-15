package de.kamiql.Dashboard.impl.bot.listener.message;

import de.kamiql.Dashboard.impl.bot.listener.EventListener;
import de.kamiql.Dashboard.impl.bot.listener.MessageListener;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageUpdateListener extends MessageListener implements EventListener<MessageUpdateEvent> {

    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
        return Mono.just(event)
          .filter(MessageUpdateEvent::isContentChanged)
          .flatMap(MessageUpdateEvent::getMessage)
          .flatMap(super::processCommand);
    }
}
