package com.kafka.consumer.consumer.Entities;

import lombok.*;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LibraryEvent {
    @Id
    @GeneratedValue
    private Integer LibraryEventId;
    @Enumerated(EnumType.STRING)
    private LibraryEventType libraryEventType;
    @OneToOne(mappedBy = "libraryEvent",cascade = {CascadeType.ALL})
    @ToString.Exclude
    private Book book;
}
