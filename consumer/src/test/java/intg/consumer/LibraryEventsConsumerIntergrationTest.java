
package consumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.consumer.consumer.consumer.LibraryEventConsumer;
import com.kafka.consumer.consumer.repos.LibraryEventsRepository;
import com.kafka.consumer.consumer.service.LibraryEventsService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;



@EmbeddedKafka(topics = {"library-events"},partitions = 3 )
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsConsumerIntergrationTest {
    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @SpyBean
    LibraryEventConsumer libraryEventsConsumerSpy;

    @SpyBean
    LibraryEventsService libraryEventsServiceSpy;

    @Autowired
    LibraryEventsRepository libraryEventsRepository;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @BeforeEach
    void setUp() {

        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()){
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }
       @Test
      void publishNewEventLibrary()throws ExecutionException, InterruptedException, JsonProcessingException{
            String json = " {\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
            kafkaTemplate.sendDefault(json).get();

            CountDownLatch latch = new CountDownLatch(1);
            latch.await(3, TimeUnit.SECONDS);

            verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
            verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));
        }


    }


