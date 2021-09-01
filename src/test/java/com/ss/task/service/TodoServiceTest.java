package com.ss.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ss.task.api.exception.ForbiddenRequestException;
import com.ss.task.api.exception.InvalidRequestException;
import com.ss.task.api.exception.TodoNotFoundException;
import com.ss.task.entity.Todo;
import com.ss.task.mapper.TodoMapper;
import com.ss.task.model.JsonPatchOperation;
import com.ss.task.model.Operation;
import com.ss.task.model.TodoInfo;
import com.ss.task.model.TodoStatus;
import com.ss.task.repository.TodoRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    private static Todo entity1;
    private static Todo entity2;
    private static TodoInfo request1;
    private static TodoInfo request2;

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoService todoService;

    @BeforeAll
    public static void init() {
        entity1 = new Todo(1, "test description 1", TodoStatus.NOT_DONE.name(), LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now());
        entity2 = new Todo(2, "test description 2", TodoStatus.PAST_DUE.name(), LocalDateTime.now(),
                LocalDateTime.of(2020, 1, 1, 1, 1), LocalDateTime.now());
        request1 = new TodoInfo().description("test description 1").status(TodoStatus.NOT_DONE)
                .dueDate(LocalDateTime.of(2024, 1, 1, 1, 1)).created(LocalDateTime.now());
        request2 = new TodoInfo().description("test description 1").status(TodoStatus.PAST_DUE).dueDate(LocalDateTime.now())
                .created(LocalDateTime.now());
    }

    @Test
    public void testUpdateTodoItem_ValidRequest() {

        final var fieldToChange = "/status";
        final var newStatus = TodoStatus.DONE.name();
        final var newStatusDate = LocalDateTime.now();
        final var updatedEntity =
                new Todo(entity1.getId(), entity1.getDescription(), newStatus, LocalDateTime.now(), LocalDateTime.now(),
                        newStatusDate);
        Mockito.when(todoRepository.findById(1)).thenReturn(Optional.of(entity1));
        Mockito.when(todoRepository.save(any())).thenReturn(updatedEntity);
        Mockito.when(todoMapper.map(updatedEntity)).thenReturn(
                request1.status(TodoStatus.valueOf(updatedEntity.getStatus())).statusDate(updatedEntity.getStatusDate()));

        final JsonPatchOperation jsonPatchOperation =
                new JsonPatchOperation().op(Operation.REPLACE).path(fieldToChange).value(newStatus);

        final var response = todoService.updateTodoItem(1, List.of(jsonPatchOperation));

        assertThat(response.getStatus().name()).isEqualTo(newStatus);
        assertThat(response.getStatusDate()).isEqualTo(newStatusDate);

        verify(todoRepository, Mockito.times(1)).save(any());

    }

    @Test
    public void testUpdateTodoItem_InvalidRequest() {

        final var fieldToChange = "/xyz";
        final var newStatus = TodoStatus.DONE.name();

        Mockito.when(todoRepository.findById(1)).thenReturn(Optional.of(entity1));
        final JsonPatchOperation jsonPatchOperation =
                new JsonPatchOperation().op(Operation.REPLACE).path(fieldToChange).value(newStatus);

        assertThrows(InvalidRequestException.class, () -> todoService.updateTodoItem(1, List.of(jsonPatchOperation)));

    }

    @Test
    public void testUpdateTodoItem_ForbiddenRequest() {

        final var fieldToChange = "/status";
        final var newStatus = TodoStatus.DONE.name();

        Mockito.when(todoRepository.findById(2)).thenReturn(Optional.of(entity2));
        final JsonPatchOperation jsonPatchOperation =
                new JsonPatchOperation().op(Operation.REPLACE).path(fieldToChange).value(newStatus);

        assertThrows(ForbiddenRequestException.class, () -> todoService.updateTodoItem(2, List.of(jsonPatchOperation)));

    }

    @Test
    public void testAddTodoItem_validRequest() {

        Mockito.when(todoMapper.map(request1)).thenReturn(entity1);
        Mockito.when(todoRepository.save(entity1)).thenReturn(entity1);
        Mockito.when(todoMapper.map(entity1)).thenReturn(request1.id(entity1.getId()));

        todoService.addTodoItem(request1);

        assertThat(request1.getId()).isEqualTo(entity1.getId());
        verify(todoRepository, Mockito.times(1)).save(entity1);

    }

    @Test
    public void testAddTodoItem_invalidRequest() {

        assertThrows(InvalidRequestException.class, () -> todoService.addTodoItem(request2));

    }

    @Test
    public void testGetTodoItem_validRequest() {

        Mockito.when(todoRepository.findById(1)).thenReturn(Optional.of(entity1));
        Mockito.when(todoMapper.map(entity1)).thenReturn(request1.id(entity1.getId()));

        todoService.getTodoItem(1);

        assertThat(request1.getId()).isEqualTo(entity1.getId());
        verify(todoRepository, Mockito.times(1)).findById(1);

    }

    @Test
    public void testGetTodoItem_NotFound() {

        Mockito.when(todoRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.getTodoItem(3));

    }

}
