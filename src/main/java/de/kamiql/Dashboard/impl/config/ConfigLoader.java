package de.kamiql.Dashboard.impl.config;

import de.kamiql.Dashboard.DashboardApplication;
import lombok.NoArgsConstructor;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URISyntaxException;

@NoArgsConstructor
public class ConfigLoader {
    public static YamlConfiguration loadConfig(String resourcePath) throws IOException, InvalidConfigurationException, URISyntaxException {
        YamlConfiguration config = new YamlConfiguration();

        InputStream inputStream = DashboardApplication.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Die Ressource konnte nicht gefunden werden: " + resourcePath);
        }

        Path tempFile = Files.createTempFile("config", ".tmp");
        Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        config.load(tempFile.toFile());

        tempFile.toFile().deleteOnExit();

        return config;
    }
}
