package com.kafka.consumer.consumer.Entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
    @OneToOne
    @JoinColumn(name ="LibraryEventId")
    private LibraryEvent libraryEvent;
}
