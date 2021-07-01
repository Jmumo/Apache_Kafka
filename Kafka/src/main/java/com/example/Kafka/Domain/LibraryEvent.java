package com.example.Kafka.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryEvent {
    private Integer LibraryEventId;
    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;
}
