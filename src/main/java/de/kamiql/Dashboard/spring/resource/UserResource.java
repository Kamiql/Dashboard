package de.kamiql.Dashboard.spring.resource;

import de.kamiql.Dashboard.spring.domain.User;
import de.kamiql.Dashboard.spring.repo.UserRepository;
import de.kamiql.Dashboard.spring.service.AvatarService;
import de.kamiql.Dashboard.spring.service.UserService;
import de.kamiql.Dashboard.spring.util.MultipartImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static de.kamiql.Dashboard.spring.constants.Constant.AVATAR_DIRECTORY;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Object> registerUser(@RequestBody User user, @RequestParam String secret) throws IOException {
        BufferedImage avatarImage = avatarService.generateAvatar(user.getUsername());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(avatarImage, "PNG", byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        MultipartFile avatarFile = new MultipartImage(byteArray, "avatar.png", "image/png");

        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken!");
        }

        user.setId(UUID.randomUUID().toString());
        user.setParent("DEFAULT");
        user.setCreated(new Date());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAvatarUrl(userService.saveAvatar(user.getId(), avatarFile));
        User registeredUser = userService.registerUser(user, secret);
        if (registeredUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid secret!");
        }
        return ResponseEntity.created(URI.create("/api/v1/user/" + user.getId()))
                             .body(registeredUser.harmless());
    }

    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam User user) {
        return ResponseEntity.ok(userService.getUserById(user.getId()).harmless());
    }

    @GetMapping("/session")
    public ResponseEntity<User> currentUser(Authentication authentication) {
        User user = userRepository.findUserByUsername(authentication.getName()).orElse(User.DEFAULT);
        return ResponseEntity.ok(user.harmless());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id).harmless());
    }

    @PutMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(id, file));
    }

    @GetMapping(path = "/avatar/{filename}", produces = {
        MediaType.IMAGE_PNG_VALUE,
        MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_GIF_VALUE
    })
    public byte[] getAvatar(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(AVATAR_DIRECTORY + filename));
    }
}
