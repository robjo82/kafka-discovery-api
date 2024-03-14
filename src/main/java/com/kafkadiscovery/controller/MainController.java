package com.kafkadiscovery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {
     
   @PostMapping("/test")
    public ResponseEntity<String> uploadFile(@RequestParam String file) {
        // Ici, vous traiteriez le fichier XML reçu
        // Par exemple, en le parseant et en extrayant les données nécessaires

        // return new ResponseEntity<>(mq.toPrettyString(), HttpStatus.OK);
        return new ResponseEntity<>("message bien recu",HttpStatus.OK);
    }
}
