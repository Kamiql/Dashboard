package de.kamiql.Dashboard.impl.bot.listener;

import de.kamiql.Dashboard.impl.logs.Logger;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {

    Logger LOG = new Logger();

    Class<T> getEventType();
    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        LOG.error(getEventType().getSimpleName(), error.getMessage());
        return Mono.empty();
    }
}
