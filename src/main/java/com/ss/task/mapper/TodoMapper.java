package com.ss.task.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.ss.task.entity.Todo;
import com.ss.task.model.TodoInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    Todo map(TodoInfo todoInfo);

    TodoInfo map(Todo todo);

    List<TodoInfo> map(List<Todo> todoList);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "statusDate", source = "statusDate")
    void mapNewStatus(@MappingTarget Todo todo, String status, LocalDateTime statusDate);
}
