package de.kamiql.Dashboard.spring.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Document("user")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Getter
@Setter
@Builder
public class User {
    @Id
    @ReadOnlyProperty
    private String id;

    private String username;
    private String password;
    private String avatarUrl;

    private String parent;
    private Date created;

    /**
     * Creates a "harmless" copy of the user, removing sensitive data like the password.
     *
     * @return a new User object with sensitive fields (e.g., password) removed.
     */
    public User harmless() {
        User user = this.copy();
        user.setPassword(null);
        return user;
    }

    /**
     * Creates a deep copy of the current user instance.
     *
     * @return a new User object with the same field values as the original.
     */
    public User copy() {
        return new User(
            this.id,
            this.username,
            this.password,
            this.avatarUrl,
            this.parent,
            this.created
        );
    }

    public static final User DEFAULT = User
            .builder()
            .id(String.valueOf(UUID.randomUUID()))
            .username("DEFAULT")
            .password("DEFAULT")
            .avatarUrl("DEFAULT")
            .parent("USER")
            .created(new Date())
            .build();
}
