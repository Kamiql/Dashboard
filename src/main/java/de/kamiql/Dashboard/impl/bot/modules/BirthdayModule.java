package de.kamiql.Dashboard.impl.bot.modules;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import de.kamiql.Dashboard.impl.config.ConfigLoader;
import lombok.Builder;
import lombok.Getter;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.conversions.Bson;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BirthdayModule {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void saveBirthday(Birthday birthday) {
        MongoCollection<Document> collection = connect();
        Document newDoc = new Document("user_id", birthday.getUserID())
                .append("guild_id", birthday.getGuildID())
                .append("date", birthday.getDate());

        Bson filter = Filters.and(Filters.eq("birthday.user_id", birthday.getUserID()), Filters.eq("birthday.guild_id", birthday.getGuildID()));
        collection.updateOne(filter, new Document("$set", new Document("birthday", newDoc)), new UpdateOptions().upsert(true));
    }

    public List<Birthday> getAllBirthdays() {
        MongoCollection<Document> collection = connect();
        FindIterable<Document> iterable = collection.find();
        List<Birthday> birthdays = new ArrayList<>();
        for (Document doc : iterable) {
            Document birthdayDoc = (Document) doc.get("birthday");
            Birthday birthday = Birthday.builder()
                    .userID(birthdayDoc.getString("user_id"))
                    .guildID(birthdayDoc.getString("guild_id"))
                    .date(birthdayDoc.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .build();
            birthdays.add(birthday);
        }
        return birthdays;
    }

    public Optional<Birthday> getBirthday(String userID) {
        MongoCollection<Document> collection = connect();
        Document doc = collection.find(Filters.eq("birthday.user_id", userID)).first();
        if (doc != null) {
            Document birthdayDoc = (Document) doc.get("birthday");
            Birthday birthday = Birthday.builder()
                    .userID(birthdayDoc.getString("user_id"))
                    .guildID(birthdayDoc.getString("guild_id"))
                    .date(birthdayDoc.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .build();
            return Optional.of(birthday);
        }
        return Optional.empty();
    }

    private MongoCollection<Document> connect() {
        String uri = "mongodb://localhost:27017/Dashboard";
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            String databaseName = uri.substring(uri.lastIndexOf("/") + 1);
            return mongoClient.getDatabase(databaseName).getCollection("birthday");
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei der Verbindung mit MongoDB: " + e.getMessage(), e);
        }
    }

    @Getter
    @Builder
    public static class Birthday {
        private String userID;
        private String guildID;
        private LocalDate date;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InvalidConfigurationException {
        YamlConfiguration config = ConfigLoader.loadConfig("config/bot.yml");
        System.out.println(config.getString("bot.module.birthdays.mongodb.uri"));
    }
}
