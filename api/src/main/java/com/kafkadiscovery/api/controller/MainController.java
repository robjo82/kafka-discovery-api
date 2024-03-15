package com.kafkadiscovery.api.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kafkadiscovery.api.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final KafkaSender kafkaSender;

   @PostMapping("/data")
   public ResponseEntity<String> uploadFile(@RequestBody String xmlData) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xmlData);
            JsonNode rootNode = mapper.readTree(json);

           
            // Ajoutez la logique pour PV1 si nécessaire
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(node);
            kafkaSender.send("topic1", json); 
            
            JsonNode mshNode = rootNode.path("MSH");
            JsonNode pidNode = rootNode.path("PID");
            // Extrait les données MSH
            String msh9 = mshNode.path("MSH.9").path("MSH.9.1").asText();
    
            // Extrait les données PID
            String pid3 = pidNode.path("PID.3").path("PID.3.1").asText();
            String patientName = pidNode.path("PID.5").get(0).path("PID.5.1").asText();
            String patientSurname = pidNode.path("PID.5").get(0).path("PID.5.2").asText();
            // log.info(json);
            log.info("-----------------");
            log.info(" ");
            // Afficher les résultats
            log.info("MSH-9: " + msh9);
            log.info("PID-3 (Patient ID): " + pid3);
            log.info("Patient Name: " + patientName);
            log.info("Patient Surname: " + patientSurname);

            
            
            // JsonNode pidNode = node.get("PID");
            // JsonNode personNode = pidNode.get("PID.5").get(0);
            // JsonNode addressNode = pidNode.get("PID.11").get(0);
            
            log.info(personNode);
            log.info(addressNode);
            log.info(" ");
            log.info("--------------------");
            return ResponseEntity.ok(json);

       } catch (IOException e) {
           log.error("Erreur lors de la conversion du fichier XML en JSON", e);
            return ResponseEntity.status(500).body("Erreur lors de la conversion du fichier XML en JSON");
       }
    }
}

