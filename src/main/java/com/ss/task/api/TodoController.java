package com.ss.task.api;

import java.util.List;

import com.ss.task.model.JsonPatchOperation;
import com.ss.task.model.TodoInfo;
import com.ss.task.service.TodoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TodoController implements TodoApi {

    private final TodoService todoService;

    @Override
    public ResponseEntity<TodoInfo> addTodoItem(final TodoInfo todoInfo) {
        return ResponseEntity.ok(todoService.addTodoItem(todoInfo));
    }

    @Override
    public ResponseEntity<TodoInfo> getTodoItem(final Integer id) {
        return ResponseEntity.ok(todoService.getTodoItem(id));
    }

    @Override
    public ResponseEntity<List<TodoInfo>> getTodoItems(final String status) {
        return ResponseEntity.ok(todoService.getTodoItems(status));
    }

    @Override
    public ResponseEntity<TodoInfo> updateTodoItem(final Integer id, final List<JsonPatchOperation> jsonPatchOperations) {
        return ResponseEntity.ok(todoService.updateTodoItem(id, jsonPatchOperations));
    }
}
