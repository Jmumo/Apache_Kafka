package com.example.Kafka.Controllers;

import com.example.Kafka.Domain.Book;
import com.example.Kafka.Domain.LibraryEvent;
import com.example.Kafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.AbstractDocument;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void PostEventLibrary() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(1234)
                .bookAuthor("mumo")
                .bookName("UnderWorld")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(book)
                .build();
          String json = objectMapper.writeValueAsString(libraryEvent);

//    when(libraryEventProducer).sendLibraryEvent_approach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEvent_approach2(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/v1/libraryevent")
                                  .content(json)
                                   .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }
     @Test
    void PostEventLibrary_4xx() throws Exception {
        //given

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(null)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);

//        doNothing().when(libraryEventProducer).sendLibraryEvent_approach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEvent_approach2(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }


    @Test
    void PutEventLibrary_4xx() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(1234)
                .bookAuthor("mumo")
                .bookName("UnderWorld")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .LibraryEventId(null)
                .book(book)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEvent_approach2(isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(put("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError()
        ).andExpect(content().string("please provide event id"));
    }




}
