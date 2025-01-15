package de.kamiql.Dashboard.impl.bot.util;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GlobalCommandRegistrar {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestClient restClient;

    public void registerCommands(List<Resource> resources) throws IOException {
        final JacksonResources d4jMapper = JacksonResources.create();

        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        List<ApplicationCommandRequest> commands = new ArrayList<>();
        for (Resource resource : resources) {
            String json = getResourceFileAsString(resource);
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                .readValue(json, ApplicationCommandRequest.class);

            commands.add(request);
        }

        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
            .doOnNext(cmd -> LOGGER.debug("Successfully registered Global Command {}", cmd.name()))
            .doOnError(e -> LOGGER.error("Failed to register global commands", e))
            .subscribe();
    }

    private static String getResourceFileAsString(Resource resource) throws IOException {
        if (resource == null || !resource.exists()) {
            throw new IOException("Resource not found.");
        }

        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
