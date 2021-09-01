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

public class StepDefs extends CucumberSpringConfiguration {

    @Autowired
    private MockMvc mockMvc;

    private MvcResult mvcResult;

    @Given("todo item is created using {string} at {string}")
    public void initializeTodoStore(final String bodyFilename, final String path) throws Throwable {
        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, path)
                .content(readFile("classpath:__files/requests/" + bodyFilename)).contentType(APPLICATION_JSON));
    }

    @When("a {string} request is received by the todo service on the endpoint {string} with {string}")
    public void sendRequest(final String httpMethod, final String path, final String requestBody) throws Throwable {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.valueOf(httpMethod), path)
                .content(readFile("classpath:__files/requests/" + requestBody))
                .contentType(httpMethod.equals(HttpMethod.POST.name()) ? APPLICATION_JSON_VALUE : "application/json-patch+json"))
                .andReturn();
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
        assertThatJson(mvcResult.getResponse().getContentAsString(UTF_8)).whenIgnoringPaths("statusDate")
                .isEqualTo(readFile("classpath:__files/responses/" + expectedBodyFile));
    }
}