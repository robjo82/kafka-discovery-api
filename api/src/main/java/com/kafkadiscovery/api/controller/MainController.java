package com.kafkadiscovery.api.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kafkadiscovery.api.service.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;


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
            JsonNode node = xmlMapper.readTree(xmlData.getBytes());
            JsonNode rootNode = xmlMapper.readTree(xmlData);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(node);
            

            
            // Création d'un nouvel ObjectMapper pour manipuler les données JSON
            ObjectNode personNode = objectMapper.createObjectNode();

            // Extraction des informations du JSON original
            String patientId = rootNode.path("PID").path("PID.3").path("PID.3.1").asText();
            String patientFirstName = rootNode.path("PID").path("PID.5").get(0).path("PID.5.1").asText();
            String patientLastName = rootNode.path("PID").path("PID.5").get(0).path("PID.5.2").asText();

            // Ajout des informations dans le nouvel objet JsonNode sous la catégorie "personne"
            personNode.put("id", patientId);
            personNode.put("prenom", patientFirstName);
            personNode.put("nom", patientLastName);

            // Création de l'objet JSON final avec la nouvelle catégorie "personne"
            ObjectNode finalJson = objectMapper.createObjectNode();
            finalJson.set("personne", personNode);

            // Conversion de l'objet JsonNode en chaîne JSON pour l'affichage ou d'autres utilisations
            String jsonString = objectMapper.writeValueAsString(finalJson);
            System.out.println(jsonString);

            
            kafkaSender.send("topic1", json); 

            
            
            return ResponseEntity.ok(json);
       } catch (IOException e) {
           log.error("Erreur lors de la conversion du fichier XML en JSON", e);
            return ResponseEntity.status(500).body("Erreur lors de la conversion du fichier XML en JSON");
       }
    }
}

