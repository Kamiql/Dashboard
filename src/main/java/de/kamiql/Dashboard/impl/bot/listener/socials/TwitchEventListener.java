package de.kamiql.Dashboard.impl.bot.listener.socials;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.kamiql.Dashboard.impl.config.ConfigLoader;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@Component("twitch_eventsub")
public class TwitchEventListener {
    private final GatewayDiscordClient client;

    private final String clientId;
    private final String clientSecret;
    private final String streamer;
    private final String channelId;

    public TwitchEventListener(GatewayDiscordClient client) throws IOException, URISyntaxException, InvalidConfigurationException {
        YamlConfiguration config = ConfigLoader.loadConfig("config/bot.yml");

        this.clientId = config.getString("bot.module.socials.twitch.client-id");
        this.clientSecret = config.getString("bot.module.socials.twitch.client-secret");
        this.streamer = config.getString("bot.module.socials.twitch.streamer");
        this.channelId = config.getString("bot.module.socials.twitch.channel-id");

        this.client = client;
        initializeTwitchClient();
    }

    private void initializeTwitchClient() {
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build();

        twitchClient.getClientHelper().enableStreamEventListener(streamer);

        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, this::onStreamGoLive);
    }

    private void onStreamGoLive(ChannelGoLiveEvent event) {
        EmbedCreateSpec embed = createEmbed(event);
        sendMessage(embed);
    }

    private EmbedCreateSpec createEmbed(ChannelGoLiveEvent event) {
        return EmbedCreateSpec.builder()
            .color(Color.CYAN)
            .title(event.getStream().getTitle())
            .url("https://www.twitch.tv/" + event.getChannel().getName())
            .author(event.getChannel().getName() + " is now live on Twitch",
                    "https://www.twitch.tv/" + event.getChannel().getName(),
                    getUserAvatar())
            .addField("Thema", event.getStream().getGameName(), true)
            .addField("Viewer", String.valueOf(event.getStream().getViewerCount()), true)
            .image(event.getStream().getThumbnailUrl(1920, 1080))
            .timestamp(Instant.now())
            .footer("Twitch Live Alert", client.getSelf().block().getAvatarUrl())
            .build();
    }

    private void sendMessage(EmbedCreateSpec embed) {
        client.getChannelById(Snowflake.of(channelId))
                .ofType(GuildMessageChannel.class)
                .flatMap(channel -> channel.createMessage(
                    MessageCreateSpec.builder()
                            .content("@everyone")
                            .addEmbed(embed)
                            .build()
                )).subscribe();
    }

    public String getOAuthToken() {
        try {
            String url = "https://id.twitch.tv/oauth2/token";
            String params = "client_id=" + clientId +
                            "&client_secret=" + clientSecret +
                            "&grant_type=client_credentials";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
            return json.get("access_token").getAsString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error getting OAuth token: " + e.getMessage(), e);
        }
    }

    public String getUserAvatar() {
        try {
            String token = getOAuthToken();
            String url = "https://api.twitch.tv/helix/users?login=" + streamer;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
            JsonArray data = json.getAsJsonArray("data");
            if (data != null && !data.isEmpty()) {
                return data.get(0).getAsJsonObject().get("profile_image_url").getAsString();
            }
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error getting user avatar: " + e.getMessage(), e);
        }
    }
}
