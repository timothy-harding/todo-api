package com.ss.task;

import static com.ss.task.utils.JsonUtils.readFile;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class StepDefinitions extends CucumberSpringConfiguration {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    private static final String FILES_REQUESTS = "classpath:__files/requests/";
    private static final String FILES_RESPONSES = "classpath:__files/responses/";
    private static final String APPLICATION_JSON_PATCH_VALUE = "application/json-patch+json";

    @Given("todo item is created using {string} at {string}")
    public void initializeTodoStore(final String bodyFilename, final String path) throws Throwable {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, path).content(readFile(FILES_REQUESTS + bodyFilename))
                .contentType(APPLICATION_JSON));
    }

    @When("a {string} request is received by the todo service on the endpoint {string} with {string}")
    public void sendRequest(final String httpMethod, final String path, final String requestBody) throws Throwable {
        final var mediaType = httpMethod.equals(HttpMethod.POST.name()) ? APPLICATION_JSON_VALUE : APPLICATION_JSON_PATCH_VALUE;
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.valueOf(httpMethod), path)
                .content(readFile(FILES_REQUESTS + requestBody)).contentType(mediaType)).andReturn();
    }

    @When("a {string} request is received by the todo service on the endpoint {string}")
    public void sendRequestWithoutBody(final String httpMethod, final String path) throws Throwable {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.valueOf(httpMethod), path)).andReturn();
    }

    @Then("the response status is {int}")
    public void assertResponseStatus(final int responseStatus) {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(responseStatus);
    }

    @Then("response body is equal to {string}")
    public void assertContent(final String expectedBodyFile) throws Throwable {
        assertThatJson(mvcResult.getResponse().getContentAsString(UTF_8))
                .whenIgnoringPaths("statusDate") //to be ignored as this field is dynamic and time significant
                .isEqualTo(readFile(FILES_RESPONSES + expectedBodyFile));
    }
}