package de.kamiql.Dashboard.impl.bot.listener.commands;

import de.kamiql.Dashboard.impl.bot.listener.EventListener;
import de.kamiql.Dashboard.impl.bot.modules.BirthdayModule;
import de.kamiql.Dashboard.impl.logs.Logger;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class BirthdayCommandListener implements EventListener<ChatInputInteractionEvent> {

    /**
     * Event-Handler für den Subcommand 'remember'
     */
    public Mono<Void> handleRememberBirthday(ChatInputInteractionEvent event) {
        event.deferReply().withEphemeral(true).block();

        String birthdate = event.getOption("remember")
                .flatMap(subcommand -> subcommand.getOption("date"))
                .flatMap(option -> option.getValue().map(ApplicationCommandInteractionOptionValue::asString))
                .orElse("");

        if (birthdate.isEmpty() || !birthdate.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title("Error!")
                    .description("Please provide a valid date in the format 'DD.MM.YYYY'.")
                    .color(Color.RED)
                    .build();

            return event.createFollowup()
                    .withEphemeral(true)
                    .withEmbeds(embed).then();
        }

        try {
            LocalDate date = LocalDate.parse(birthdate, BirthdayModule.formatter);

            return Mono.fromRunnable(() -> {
                new BirthdayModule().saveBirthday(
                        BirthdayModule.Birthday.builder()
                                .date(date)
                                .userID(event.getInteraction().getUser().getId().asString())
                                .guildID(event.getInteraction().getGuildId().get().asString())
                                .build()
                );
            })
            .thenReturn(EmbedCreateSpec.builder()
                    .title("Birthday Saved!")
                    .description("Your birthday has been saved as " + date.format(BirthdayModule.formatter))
                    .color(Color.CYAN)
                    .build())
            .flatMap(embed -> event.createFollowup()
                    .withEphemeral(true)
                    .withEmbeds(embed)).then();

        } catch (DateTimeParseException e) {
            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title("Error!")
                    .description("The date " + birthdate + " is not valid!")
                    .color(Color.RED)
                    .build();

            return event.createFollowup()
                    .withEphemeral(true)
                    .withEmbeds(embed).then();
        }
    }

    /**
     * Event-Handler für den Subcommand 'get'
     */
    public Mono<Void> handleGetBirthday(ChatInputInteractionEvent event) {
        event.deferReply().withEphemeral(true).block();

        User user = event.getOption("get")
                .flatMap(subcommand -> subcommand.getOption("user"))
                .flatMap(option -> option.getValue().map(val -> (User) val.asUser().block()))
                .orElse(null);

        if (user == null) {
            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title("Error!")
                    .description("User not found.")
                    .color(Color.RED)
                    .build();

            return event.createFollowup()
                    .withEphemeral(true)
                    .withEmbeds(embed).then();
        }

        return Mono.fromCallable(() -> new BirthdayModule().getBirthday(user.getId().asString()))
                .flatMap(birthday -> {
                    if (birthday.isEmpty()) {
                        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                .title("No Birthday Found!")
                                .description("The requested user has not set a birthday.")
                                .color(Color.RED)
                                .build();

                        return event.createFollowup()
                                .withEphemeral(true)
                                .withEmbeds(embed);
                    }

                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                            .title("Birthday Information")
                            .description("Here is the birthday information for the requested user.")
                            .addField("User", "<@" + user.getId().asString() + ">", true)
                            .addField("Birthday",
                                "[" + birthday.get().getDate().format(BirthdayModule.formatter) + "](https://www.timeanddate.com/on-this-day/"
                                + birthday.get().getDate().format(DateTimeFormatter.ofPattern("MMMM/d", Locale.ENGLISH)) + ")",
                                true)
                            .color(Color.CYAN)
                            .build();


                    return event.createFollowup()
                            .withEphemeral(true)
                            .withEmbeds(embed);
                }).then();
    }

    /**
     * Gibt den Typ des Events zurück, das dieser Listener behandelt.
     */
    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }

    /**
     * Führt das Event aus und delegiert basierend auf dem Subcommand.
     */
    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String subcommand = event.getOptions().get(0).getName();

        return switch (subcommand) {
            case "remember" -> handleRememberBirthday(event);
            case "get" -> handleGetBirthday(event);
            default -> {
                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                        .title("Error!")
                        .description("Unknown subcommand: " + subcommand)
                        .color(Color.RED)
                        .build();
                yield event.reply()
                        .withEphemeral(true)
                        .withEmbeds(embed);
            }
        };
    }

    /**
     * Fehlerbehandlung für alle aufgetretenen Fehler.
     */
    @Override
    public Mono<Void> handleError(Throwable error) {
        new Logger().fatal("DISCORD", error.getMessage());
        return Mono.empty();
    }
}
