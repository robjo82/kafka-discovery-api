package com.kafkadiscovery.api.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kafkadiscovery.api.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final KafkaSender kafkaSender;
     
   @PostMapping("/data")
   public ResponseEntity<String> uploadFile(@RequestBody MultipartFile xmlData) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xmlData.getInputStream());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(node);
            kafkaSender.send("topic1", json);
            log.info(json);

            return ResponseEntity.ok(json);

       } catch (IOException e) {
           log.error("Erreur lors de la conversion du fichier XML en JSON", e);
            return ResponseEntity.status(500).body("Erreur lors de la conversion du fichier XML en JSON");
       }
    }
}

