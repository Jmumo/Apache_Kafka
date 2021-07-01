package com.example.Kafka.Domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @NotNull
    private Integer bookId;
    @NotEmpty
    private String bookName;
    @NotEmpty
    private String bookAuthor;
}
