package com.practice.portcontainertrackingbackend.unit.presentation.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.portcontainertrackingbackend.application.ContainerService;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.exception.ContainerException;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest
public class ContainerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
            int containerId = 1;
            container.setId(containerId);
            given(containerService.createContainer(any(Container.class)))
                    .willAnswer(arguments -> arguments.getArgument(0));

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
            given(containerService.createContainer(any(Container.class)))
                    .willThrow(new RuntimeException("Unexpected error for create container"));

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
            given(containerService.createContainer(any(Container.class)))
                    .willThrow(new IllegalArgumentException("Error in arguments"));

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
            int containerId = 1;
            container.setId(containerId);
            given(containerService.getContainer(containerId)).willReturn(Optional.of(container));

            // When
            ResultActions response = mockMvc.perform(get(serviceDetailUrl, containerId));
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
            int containerId = 1;
            given(containerService.getContainer(containerId)).willReturn(Optional.empty());

            // When
            ResultActions response = mockMvc.perform(get(serviceDetailUrl, containerId));

            // Then
            response.andExpect(status().isNotFound());
        }
    }

    @Nested
    class ListContainer {
        @Test
        void shouldReturnContainerListWhenContainersExist() throws Exception {
            // Given
            Container container2 = generateContainer();
            List<Container> containers = List.of(container, container2);

            given(containerService.getAllContainers()).willReturn(containers);

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(containers.size())));
        }

        @Test
        void shouldReturnEmptyContainerListWhenNoContainersExist() throws Exception {
            // Given
            given(containerService.getAllContainers()).willReturn(Collections.emptyList());

            // When
            ResultActions response = mockMvc.perform(get(serviceListUrl));

            // Then
            response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(0)));
        }
    }

    @Nested
    class UpdateContainer {
        @Test
        void shouldUpdateWhenObjectExistAndValid() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            given(containerService.updateContainer(anyInt(), any(Container.class)))
                    .willAnswer(arguments -> arguments.getArgument(1));

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code", is(newContainer.getCode())))
                    .andExpect(jsonPath("$.status", is(newContainer.getStatus().toString())));
        }

        @Test
        void shouldThrowExceptionWhenUpdateNoExistingContainer() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            given(containerService.updateContainer(anyInt(), any(Container.class)))
                    .willThrow(ContainerException.ContainerNotFoundException.class);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isNotFound());
        }

        @Test
        void shouldThrowExceptionWhenErrorSavingContainer() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            given(containerService.updateContainer(anyInt(), any(Container.class)))
                    .willThrow(RuntimeException.class);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().is5xxServerError());
        }

        @Test
        void shouldThrowExceptionWhenInvalidStatusInContainerUpdate() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            given(containerService.updateContainer(anyInt(), any(Container.class)))
                    .willThrow(IllegalArgumentException.class);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isBadRequest());
        }

        @Test
        void shouldNotUpdateWhenNullAttributes() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            newContainer.setCode(null);
            newContainer.setStatus(null);
            given(containerService.updateContainer(anyInt(), any(Container.class)))
                    .willReturn(container);

            // When
            ResultActions response = mockMvc.perform(put(serviceUpdateUrl, containerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newContainer)));

            // Then
            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code", is(container.getCode())))
                    .andExpect(jsonPath("$.status", is(container.getStatus().toString())));
        }
    }

    @Nested
    class DeleteContainer {
        @Test
        void shouldDeleteObjectWhenExist() throws Exception {
            // Given
            int containerId = 1;
            container.setId(containerId);
            doNothing().when(containerService).deleteContainerById(containerId);

            // When
            ResultActions response = mockMvc.perform(delete(serviceDeleteUrl, containerId));

            // Then
            response.andExpect(status().isNoContent());
        }

        @Test
        void shouldNotDeleteObjectWhenNotExist() throws Exception {
            // Given
            int containerId = 1;
            doThrow(ContainerException.ContainerNotFoundException.class)
                    .when(containerService)
                    .deleteContainerById(containerId);

            // When
            ResultActions response = mockMvc.perform(delete(serviceDeleteUrl, containerId));

            // Then
            response.andExpect(status().isNotFound());
        }
    }
}
