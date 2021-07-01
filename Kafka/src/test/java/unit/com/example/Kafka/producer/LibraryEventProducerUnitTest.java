package com.example.Kafka.producer;


import com.example.Kafka.Domain.Book;
import com.example.Kafka.Domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class LibraryEventProducerUnitTest {
    @InjectMocks
    LibraryEventProducer libraryEventProducer;

     @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

     @Spy
    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void SendLibraryEvent_approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {

        Book book = Book.builder()
                .bookId(1234)
                .bookAuthor("mumo")
                .bookName("UnderWorld")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(book)
                .build();
        SettableListenableFuture settableListenableFuture = new SettableListenableFuture();
        settableListenableFuture.setException( new RuntimeException("error calling kafka"));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(settableListenableFuture);

        assertThrows(Exception.class,()->libraryEventProducer.sendLibraryEvent_approach2(libraryEvent).get());

    }

    @Test
    void SendLibraryEvent_approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {

        Book book = Book.builder()
                .bookId(1234)
                .bookAuthor("mumo")
                .bookName("UnderWorld")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(book)
                .build();
        String record = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture settableListenableFuture = new SettableListenableFuture();
//        settableListenableFuture.setException( new RuntimeException("error calling kafka"));
        TopicPartition topicPartition = new TopicPartition("library-events",1);

        RecordMetadata recordMetadata = new RecordMetadata(topicPartition,
                1,
                1,
                342,
                System.currentTimeMillis(),
                1,
                2);
      settableListenableFuture.set(new SendResult<Integer,String>(new ProducerRecord<Integer,String>("library-events",libraryEvent.getLibraryEventId(),record),recordMetadata));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(settableListenableFuture);

        ListenableFuture<SendResult<Integer,String>> listenableFuture = libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);

       SendResult<Integer,String> sendResult = listenableFuture.get();


        assert sendResult.getRecordMetadata().partition() == 1;


    }
}
