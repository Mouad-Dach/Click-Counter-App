package net.dach.clickcounterapp.controller;

import net.dach.clickcounterapp.producer.ClickProducer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Random;

@Controller
public class ClickController {
    private final ClickProducer clickProducer;

    public ClickController(ClickProducer clickProducer) {
        this.clickProducer = clickProducer;
    }

    @PostMapping("/click")
    public String click(Model model) {
        String[] users = {"user1", "user2", "user3"};
        String userId = users[new Random().nextInt(users.length)];
        clickProducer.sendClick(userId);
        model.addAttribute("message", "Click sent by user: " + userId);
        return "index"; // returns index.html
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
