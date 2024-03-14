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
   public ResponseEntity<String> uploadFile(@RequestBody String xmlData) {
       // try {
       // Création d'un XmlMapper pour lire le fichier XML
       //    XmlMapper xmlMapper = new XmlMapper();
       //    // Conversion du fichier XML en arbre JsonNode
       //    JsonNode node = xmlMapper.readTree(file.getInputStream());

       //    // Création d'un ObjectMapper pour écrire l'arbre JsonNode en tant que JSON
       //    ObjectMapper objectMapper = new ObjectMapper();
       //    String json = objectMapper.writeValueAsString(node);
       kafkaSender.send("topic1", xmlData);
       log.info(xmlData);
       // Retourne le JSON résultant

       return ResponseEntity.ok("json");
       //    } catch (IOException e) {
       //        e.printStackTrace();
       //        return ResponseEntity.status(500).body("Erreur lors de la conversion du fichier XML en JSON");
       //    }
   }
}
