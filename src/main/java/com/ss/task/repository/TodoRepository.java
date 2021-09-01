package com.ss.task.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.ss.task.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {

    List<Todo> findByStatus(String status);

    List<Todo> findByDueDateBeforeAndStatus(LocalDateTime currentDate, String status);

}
