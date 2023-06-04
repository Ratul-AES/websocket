package com.example.WebSocketServer.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class WebsocketApi {
    @GetMapping("/websocket")
    public String getHomepage() {
        return "index";
    }
}