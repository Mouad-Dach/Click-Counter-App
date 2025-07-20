package net.dach.clickcounterapp.controller;

import net.dach.clickcounterapp.consumer.ClickCountConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clicks")
public class ClickRestController {

    private final ClickCountConsumer clickCountConsumer;

    public ClickRestController(ClickCountConsumer clickCountConsumer) {
        this.clickCountConsumer = clickCountConsumer;
    }

    @GetMapping("/count")
    public Map<String, Object> getClickCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalClicks", clickCountConsumer.getTotalClicks());
        response.put("userClicks", clickCountConsumer.getUserClickCounts());
        return response;
    }
}
