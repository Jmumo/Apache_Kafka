package com.example.Kafka.producer;


import com.example.Kafka.Domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Slf4j
@Component
public class LibraryEventProducer {
    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    String topic = "library-events";

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
       ListenableFuture<SendResult<Integer,String>> listenableFuture= kafkaTemplate.sendDefault(key,value);

       listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
           @Override
           public void onFailure(Throwable ex) {
            HandleFailure(key,value,ex);
           }

           @Override
           public void onSuccess(SendResult<Integer, String> result) {
               handleSuccess(key,value,result);
           }
       });
    }


    public ListenableFuture sendLibraryEvent_approach2(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer,String> producerRecord = buildProducerRecord(key,value,topic);
//        you can as well as use this producer record
//        ListenableFuture<SendResult<Integer,String>> listenableFuture= kafkaTemplate.send("library-events",key,value);
        ListenableFuture<SendResult<Integer,String>> listenableFuture= kafkaTemplate.send(producerRecord);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                HandleFailure(key,value,ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value,result);
                log.info("message sent for key:{} and value:{} and partition:{}",key,value,result.getRecordMetadata().partition());
            }
        });
        return listenableFuture;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> RecordHeaders = List.of(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord<>(topic,null,key,value,RecordHeaders);
    }

    private void HandleFailure(Integer key, String value, Throwable ex) {
        log.error(ex.getMessage());

        try {
            throw ex;
        } catch (Throwable throwable) {
           log.error(throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("message sent for key:{} and value:{} and partition:{}",key,value,result.getRecordMetadata().partition());
    }
}
