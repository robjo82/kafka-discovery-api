package com.kafkadiscovery.api.controller;


import com.kafkadiscovery.api.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final KafkaSender kafkaSender;
     
   @PostMapping("/data")
   public ResponseEntity<String> uploadFile(@RequestParam String message) {
       kafkaSender.send("topic1", message);
       return ResponseEntity.ok(message);
    }
}
