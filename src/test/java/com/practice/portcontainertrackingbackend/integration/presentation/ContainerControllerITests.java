package com.practice.portcontainertrackingbackend.integration.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private String serviceUpdateUrl;
    private String serviceDeleteUrl;

    public Container generateContainer() {
        return Instancio.create(Container.class);
    }

    @BeforeEach
    public void setup() {
        container = generateContainer();
        serviceCreateUrl = Constants.BASE_URL + Constants.CREATE_CONTAINER_URL;
        serviceDetailUrl = Constants.BASE_URL + Constants.DETAIL_CONTAINER_URL;
        serviceListUrl = Constants.BASE_URL + Constants.LIST_CONTAINER_URL;
        serviceUpdateUrl = Constants.BASE_URL + Constants.UPDATE_CONTAINER_URL;
        serviceDeleteUrl = Constants.BASE_URL + Constants.DELETE_CONTAINER_URL;
    }

    @Nested
    class CreateContainer {
        @Test
        void shouldCreateObjectSuccessfullyAndReturn201StatusCodeForValidInput() throws Exception {
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
        void shouldReturnBadRequest400WhenCreatingInvalidObject() throws Exception {
            // Given

            // When
            ResultActions response = mockMvc.perform(post(serviceCreateUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""));

            // Then
            response.andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequest400WhenCreatingObjectWithNullFields() throws Exception {
            // Given

            // When
            ResultActions response = mockMvc.perform(post(serviceCreateUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"));

            // Then
            response.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class RetrieveContainer {
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
    }

    @Nested
    class ListContainer {
        @Test
        void shouldReturnContainerListWithOkStatusWhenContainersExist() throws Exception {
            // Given
            containerService.createContainer(container);
            containerService.createContainer(generateContainer());

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(2)));
        }

        @Test
        void shouldReturnEmptyContainerListWithOkStatusWhenNoContainersExist() throws Exception {
            // Given no containers

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(0)));
        }
    }

    @Nested
    class UpdateContainer {
        @Test
        void shouldUpdateWithOkStatusWhenObjectExistsAndIsValid() throws Exception {
            // Given
            Container savedContainer = containerService.createContainer(container);
            Container newContainer = generateContainer();

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, savedContainer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(savedContainer.getId())))
                    .andExpect(jsonPath("$.code", is(newContainer.getCode())))
                    .andExpect(jsonPath("$.status", is(newContainer.getStatus().toString())));
        }

        @Test
        void shouldReturnNotFoundWhenUpdateNoExistingContainer() throws Exception {
            // Given
            int containerId = 1;
            Container newContainer = generateContainer();

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestWhenInvalidStatusInContainerUpdate() throws Exception {
            // Given
            Container savedContainer = containerService.createContainer(container);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, savedContainer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{status: ABC}"));

            // Then
            response.andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnOkAndNotUpdateContainerWhenNullAttributes() throws Exception {
            // Given
            Container savedContainer = containerService.createContainer(container);
            Container newContainer = generateContainer();
            newContainer.setId(0);
            newContainer.setCode(null);
            newContainer.setStatus(null);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, savedContainer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(savedContainer.getId())))
                    .andExpect(jsonPath("$.code", is(savedContainer.getCode())))
                    .andExpect(
                            jsonPath("$.status", is(savedContainer.getStatus().toString())));
        }
    }

    @Nested
    class DeleteContainer {
        @Test
        void shouldReturnNoContentWhenDeleteExistContainer() throws Exception {
            // Given
            Container savedContainer = containerService.createContainer(container);

            // When
            ResultActions response = mockMvc.perform(delete(serviceDeleteUrl, savedContainer.getId()));

            // Then
            response.andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnNotFoundWhenNotExist() throws Exception {
            // Given
            int containerId = 1;

            // When
            ResultActions response = mockMvc.perform(delete(serviceDeleteUrl, containerId));

            // Then
            response.andExpect(status().isNotFound());
        }
    }
}
