package de.kamiql.Dashboard.spring.repo;

import de.kamiql.Dashboard.spring.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(String id);
}
