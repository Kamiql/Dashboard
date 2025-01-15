package de.kamiql.Dashboard.impl.bot.api;

import de.kamiql.Dashboard.impl.bot.BotConfiguration;
import de.kamiql.Dashboard.impl.bot.api.modules.Statics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bot")
public class ApiController {
    @GetMapping("/self")
    public Statics getStatus() {
        return new Statics(BotConfiguration.getClient());
    }
}
