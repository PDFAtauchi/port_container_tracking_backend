package com.practice.portcontainertrackingbackend.integration.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.portcontainertrackingbackend.application.ContainerService;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.integration.AbstractionContainerBaseTests;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@AutoConfigureMockMvc
public class ContainerControllerITests extends AbstractionContainerBaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Container container;

    private String serviceCreateUrl;
    private String serviceDetailUrl;
    private String serviceListUrl;

    public Container generateContainer() {
        container = Instancio.create(Container.class);
        return container;
    }

    @BeforeEach
    public void setup() {
        container = generateContainer();
        serviceCreateUrl = Constants.BASE_URL + Constants.CREATE_CONTAINER_URL;
        serviceDetailUrl = Constants.BASE_URL + Constants.DETAIL_CONTAINER_URL;
        serviceListUrl = Constants.BASE_URL + Constants.LIST_CONTAINER_URL;
    }

    @Test
    void shouldCreateContainerAndReturnDetails() throws Exception {
        // Given
        containerService.createContainer(container);

        // When
        ResultActions response = mockMvc.perform(post(serviceCreateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(container)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(container.getCode())))
                .andExpect(jsonPath("$.status", is(container.getStatus().toString())));
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        // Given

        // When
        ResultActions response = mockMvc.perform(
                post(serviceCreateUrl).contentType(MediaType.APPLICATION_JSON).content(""));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200OkWhenObjectExists() throws Exception {
        // Given
        Container containerInDB = containerService.createContainer(container);

        // When
        ResultActions response = mockMvc.perform(get(serviceDetailUrl, containerInDB.getId()));
        MvcResult result = response.andReturn();

        // Then
        response.andExpect(status().isOk());
        String responseData = result.getResponse().getContentAsString();
        Container containerRetrieved = objectMapper.readValue(responseData, Container.class);

        assertThat(containerRetrieved.getId()).isEqualTo(container.getId());
        assertThat(containerRetrieved.getCode()).isEqualTo(container.getCode());
        assertThat(containerRetrieved.getStatus()).isEqualTo(container.getStatus());
    }

    @Test
    void shouldReturn404NotFoundForNonexistentObject() throws Exception {
        // Given
        int containerNoExistId = 123456;

        // When
        ResultActions response = mockMvc.perform(get(serviceDetailUrl, containerNoExistId));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Nested
    class ListContainer {
        @Test
        void shouldReturnContainerListWhenContainersExist() throws Exception {
            // Given
            containerService.createContainer(container);
            containerService.createContainer(generateContainer());

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(2)));
        }

        @Test
        void shouldReturnEmptyContainerListWhenNoContainersExist() throws Exception {
            // Given no containers

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(0)));
        }
    }
}
