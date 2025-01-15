package de.kamiql.Dashboard.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {
    @GetMapping(value = {"/app/**/{path:[^.]*}", "/app/**"})
    public String forwardToReactApp() {
        return "forward:/index.html";
    }
}
