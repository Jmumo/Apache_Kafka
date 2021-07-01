package com.example.Kafka.Controllers;

import com.example.Kafka.Domain.Book;
import com.example.Kafka.Domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer,String> consumer;

    @BeforeEach
    void setUp(){
        Map<String,Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1","true",embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs,new IntegerDeserializer(),new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown(){
        consumer.close();
    }

    @Test
    @Timeout(5)
    void postLibraryEvent() throws InterruptedException {
         Book book = Book.builder()
                 .bookId(1234)
                 .bookAuthor("mumo")
                 .bookName("UnderWorld")
                 .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(book)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,httpHeaders);

      ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST,request,LibraryEvent.class);

      assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());

        ConsumerRecord<Integer,String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer,"library-events");
//        Thread.sleep(3000);
        String ExpectedRecord = "{\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1234,\"bookName\":\"UnderWorld\",\"bookAuthor\":\"mumo\"},\"libraryEventId\":null}";
         String ActualValue = consumerRecord.value();
        System.out.println(ActualValue);

        assertEquals(ExpectedRecord,ActualValue);
    }


    @Timeout(5)
    @Test
    void putLibraryEvent() throws InterruptedException {
        Book book = Book.builder()
                .bookId(1234)
                .bookAuthor("mumo")
                .bookName("UnderWorld")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(1234)
                .book(book)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,httpHeaders);

        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.PUT,request,LibraryEvent.class);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());

        ConsumerRecord<Integer,String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer,"library-events");
//        Thread.sleep(3000);
        String ExpectedRecord = "{\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1234,\"bookName\":\"UnderWorld\",\"bookAuthor\":\"mumo\"},\"libraryEventId\":1234}";
        String ActualValue = consumerRecord.value();
        System.out.println(ActualValue);

        assertEquals(ExpectedRecord,ActualValue);
    }
}
