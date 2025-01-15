package de.kamiql.Dashboard.impl.bot.listener.message;

import de.kamiql.Dashboard.impl.bot.listener.EventListener;
import de.kamiql.Dashboard.impl.bot.listener.MessageListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }
}
