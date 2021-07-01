package com.kafka.consumer.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.consumer.Entities.LibraryEvent;
import com.kafka.consumer.consumer.repos.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Optional;

@Service
@Slf4j
public class LibraryEventsService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LibraryEventsRepository libraryEventsRepository;

    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
     LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);

     log.info("library event {}",libraryEvent);

     switch(libraryEvent.getLibraryEventType()){
         case NEW:
               save(libraryEvent);
             break;
         case UPDATE:
            update(libraryEvent);
             break;

         default:
             log.info("invalid library event type");
     }

    }

    private void update(LibraryEvent libraryEvent) {
//      Optional<LibraryEvent> oldEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());

    LibraryEvent book = libraryEventsRepository.findByBook(libraryEvent);
    book.setBook(libraryEvent.getBook());
    libraryEventsRepository.save(libraryEvent);

    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);

    }
}
