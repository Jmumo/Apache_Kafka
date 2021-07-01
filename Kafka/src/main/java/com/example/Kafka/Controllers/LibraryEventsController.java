package com.example.Kafka.Controllers;

import com.example.Kafka.Domain.LibraryEvent;
import com.example.Kafka.Domain.LibraryEventType;
import com.example.Kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LibraryEventsController {
    @Autowired
    LibraryEventProducer libraryEventProducer;
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent>postLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
//        libraryEventProducer.sendLibraryEvent(libraryEvent);
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);
         return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?>putLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        if(libraryEvent.getLibraryEventId()== null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please provide event id");
        }
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}
