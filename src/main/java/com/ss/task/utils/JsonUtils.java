package com.ss.task.utils;

import static org.springframework.util.ResourceUtils.getFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.ss.task.entity.Todo;
import com.ss.task.model.JsonPatchOperation;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JsonUtils {

    public Todo applyPatch(final Todo todoEntity, final List<JsonPatchOperation> patchOperation)
            throws JsonProcessingException, JsonPatchException {
        final var mapper = getObjectMapper();
        final var writer = mapper.writer();
        final var patch = mapper.readValue(writer.writeValueAsString(patchOperation), JsonPatch.class);
        final var patched = patch.apply(mapper.convertValue(todoEntity, JsonNode.class));
        return mapper.treeToValue(patched, Todo.class);
    }

    private ObjectMapper getObjectMapper() {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public String readFile(final String filePath) throws IOException {
        final File file = getFile(filePath);
        return Files.readString(file.toPath());
    }
}
