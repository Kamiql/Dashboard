package de.kamiql.Dashboard.impl.bot;

import de.kamiql.Dashboard.impl.bot.listener.EventListener;
import de.kamiql.Dashboard.impl.bot.modules.BirthdayModule;
import de.kamiql.Dashboard.impl.bot.util.GlobalCommandRegistrar;
import de.kamiql.Dashboard.impl.config.ConfigLoader;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.util.Color;
import lombok.Getter;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Configuration
public class BotConfiguration {
    @Getter
    private static GatewayDiscordClient client;

    @Value("classpath:commands/birthday.json")
    private Resource birthdayCommand;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> EventListeners) throws Exception {
        client = DiscordClientBuilder.create(Objects.requireNonNull(ConfigLoader.loadConfig("config/bot.yml").getString("bot.token")))
            .build()
            .gateway()
                .setEnabledIntents(IntentSet.all())
            .login()
            .block();

        for(EventListener<T> listener : EventListeners) {
            client.on(listener.getEventType())
              .flatMap(listener::execute)
              .onErrorResume(listener::handleError)
              .subscribe();
        }

        new GlobalCommandRegistrar(client.getRestClient()).registerCommands(List.of(
                birthdayCommand
        ));

        return client;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkBirthday() throws Exception{
        LocalDate today = LocalDate.now();

        new BirthdayModule().getAllBirthdays().forEach(birthday -> {
            if (birthday.getDate().getMonth() == today.getMonth() && birthday.getDate().getDayOfMonth() == today.getDayOfMonth()) {
                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title("🎉 Happy Birthday!")
                    .description(String.format("Herzlichen Glückwunsch zum **%s** Geburtstag, <@%s>! 🎂🎈",today.getYear() - birthday.getDate().getYear(), birthday.getUserID()))
                    .color(Color.GREEN)
                    .footer("Feier schön!", null)
                    .build();

                try {
                    client.getChannelById(Snowflake.of(Objects.requireNonNull(ConfigLoader.loadConfig("config/bot.yml").getString("bot.module.birthdays.channel-id"))))
                            .ofType(GuildMessageChannel.class)
                            .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                                    .content(String.format("<@%s>", birthday.getUserID()))
                                    .addEmbed(embed)
                                    .build()
                            )).subscribe();
                } catch (IOException | InvalidConfigurationException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    private void updatePresence() {
        client.updatePresence(
                ClientPresence.online(
                        ClientActivity.custom(
                                String.format("Watching over %s users!", client.getGuilds()
                                    .flatMap(guild -> guild.getMembers()
                                    .filter(member -> !member.isBot())
                                    .count())
                                    .reduce(0L, Long::sum)
                                    .block()
                                )
                        )
                )
        ).subscribe();
    }
}