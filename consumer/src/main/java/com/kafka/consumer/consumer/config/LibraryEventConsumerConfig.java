package com.kafka.consumer.consumer.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Arrays;

@EnableKafka
@Configuration
@Slf4j
public class LibraryEventConsumerConfig {
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
          ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        factory.setConcurrency(3);
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//        factory.setErrorHandler(((thrownException, data) -> {
//            log.info("Exception in consumerConfig is {} and the record is {}", thrownException.getMessage(), data);
//            //persist
//        }));
//        factory.setRetryTemplate(retryTemplate());
//        factory.setRecoveryCallback((context -> {
//            if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException){
                //invoke recovery logic
//                log.info("Inside the recoverable logic");
//                Arrays.asList(context.attributeNames())
//                        .forEach(attributeName -> {
//                            log.info("Attribute name is : {} ", attributeName);
//                            log.info("Attribute Value is : {} ", context.getAttribute(attributeName));
//                        });
//
//                ConsumerRecord<Integer, String> consumerRecord = (ConsumerRecord<Integer, String>) context.getAttribute("record");
//                libraryEventsService.handleRecovery(consumerRecord);
//            }else{
//                log.info("Inside the non recoverable logic");
//                throw new RuntimeException(context.getLastThrowable().getMessage());
//            }
//
//
//            return null;
//        }));
        return factory;
    }
}
