package com.kafka.consumer.consumer.repos;

import com.kafka.consumer.consumer.Entities.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventsRepository extends JpaRepository<LibraryEvent,Integer> {
    LibraryEvent findByBook(LibraryEvent libraryEvent);
}
