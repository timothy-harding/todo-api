package com.ss.task.service;

import static com.ss.task.model.TodoStatus.NOT_DONE;
import static com.ss.task.model.TodoStatus.PAST_DUE;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.ss.task.api.exception.ForbiddenRequestException;
import com.ss.task.api.exception.InvalidRequestException;
import com.ss.task.api.exception.TodoNotFoundException;
import com.ss.task.entity.Todo;
import com.ss.task.mapper.TodoMapper;
import com.ss.task.model.JsonPatchOperation;
import com.ss.task.model.TodoInfo;
import com.ss.task.repository.TodoRepository;
import com.ss.task.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoInfo addTodoItem(final TodoInfo todoInfo) throws InvalidRequestException {
        this.validateAddRequest(todoInfo);
        final var todoEntity = todoMapper.map(todoInfo);
        return todoMapper.map(todoRepository.save(todoEntity));
    }

    public TodoInfo getTodoItem(final Integer id) throws TodoNotFoundException {
        final var todoEntity = findTodoItem(id);
        return todoMapper.map(todoEntity);
    }

    public List<TodoInfo> getTodoItems(final String status) {
        final List<Todo> todoList;
        if (status != null) {
            todoList = todoRepository.findByStatus(status);
        } else {
            todoList = todoRepository.findAll();
        }
        return todoMapper.map(todoList);
    }

    public TodoInfo updateTodoItem(final Integer id, final List<JsonPatchOperation> jsonPatchOperation) {
        final var todoEntity = getActiveTodoItem(id);
        try {
            final var updatedTodoEntity = JsonUtils.applyPatch(todoEntity, jsonPatchOperation);
            this.auditStatusChange(todoEntity, updatedTodoEntity);
            return todoMapper.map(todoRepository.save(updatedTodoEntity));
        } catch (final JsonProcessingException | JsonPatchException ex) {
            log.error("Error while applying json patch", ex);
            throw new InvalidRequestException("Invalid patch request, unable to update the todo item with id: " + id);
        }
    }

    @Scheduled(cron = "@hourly")
    public void archiveExpiredItems() {
        log.info("hourly scheduler running in the background to mark outdated todo items as past_due");
        todoRepository.findByDueDateBeforeAndStatus(now(), NOT_DONE.name()).parallelStream().forEach(this::saveAsPastDue);
    }

    private void validateAddRequest(final TodoInfo todoInfo) {
        if (Objects.equals(todoInfo.getStatus(), PAST_DUE)) {
            throw new InvalidRequestException("Invalid request, cannot add todo item with status PAST_DUE");
        }
        if (todoInfo.getDueDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException(
                    "Invalid request, cannot add todo item with past due date " + todoInfo.getDueDate());
        }
    }

    private Todo getActiveTodoItem(final Integer id) throws ForbiddenRequestException {
        final var todoEntity = findTodoItem(id);
        if (todoEntity.getStatus().equals(PAST_DUE.name())) {
            throw new ForbiddenRequestException("Forbidden, cannot update todo item with status PAST_DUE and id: " + id);
        }
        return todoEntity;
    }

    private void auditStatusChange(final Todo todoEntity, final Todo updatedTodoEntity) {
        if (!Objects.equals(todoEntity.getStatus(), updatedTodoEntity.getStatus())) {
            todoMapper.mapNewStatus(updatedTodoEntity, updatedTodoEntity.getStatus(), LocalDateTime.now());
        }
    }

    private void saveAsPastDue(final Todo todo) {
        log.info("scheduler will update the todo with id {} from status {} to {}", todo.getId(), todo.getStatus(),
                PAST_DUE.name());
        todoMapper.mapNewStatus(todo, PAST_DUE.name(), now());
        todoRepository.save(todo);
    }

    private Todo findTodoItem(final Integer id) throws TodoNotFoundException {
        return todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException("No todo item found with given id " + id));
    }

}
