package de.kamiql.Dashboard.spring.constants;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Constant {
    public static final String AVATAR_DIRECTORY = System.getProperty("user.home") + "/Dashboard/api/v1/assets/avatar/";
    public static final String[] SUPPORTED_AVATAR_TYPES = new String[] { "png", "jpg", "jpeg", "gif" };
    public static final String SESSION_SECRET = "Du7h3M72HzJF2XVqRrKrmxryjRukt196uJ9AdL3d8xVvtT5EhK6aAtTxsDZHDMRL";
        public static final SecretKey SIGN_IN_KEY = new SecretKeySpec(
        Base64.getDecoder().decode(SESSION_SECRET.getBytes(StandardCharsets.UTF_8)),
        "HmacSHA256"
    );
}
