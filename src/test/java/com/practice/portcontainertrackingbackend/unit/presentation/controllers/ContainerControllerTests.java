package com.practice.portcontainertrackingbackend.unit.presentation.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.portcontainertrackingbackend.application.ContainerServiceImpl;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
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
    private ContainerServiceImpl containerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Container container;

    private String serviceCreateUrl;
    private String serviceDetailUrl;

    public Container generateContainer() {
        container = Instancio.create(Container.class);
        return container;
    }

    @BeforeEach
    public void setup() {
        container = generateContainer();
        serviceCreateUrl = Constants.BASE_URL + Constants.CREATE_CONTAINER_URL;
        serviceDetailUrl = Constants.BASE_URL + Constants.DETAIL_CONTAINER_URL;
    }

    @Test
    public void should_return_201_status_code_and_object_created_when_create_valid_object() throws Exception {
        // Given
        int containerId = 1;
        container.setId(containerId);
        given(containerService.createContainer(any(Container.class))).willAnswer(arguments -> arguments.getArgument(0));

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
    public void should_return_400_bad_request_when_create_invalid_object() throws Exception {
        // Given
        given(containerService.createContainer(any(Container.class))).willThrow(new RuntimeException("Error"));

        // When
        ResultActions response = mockMvc.perform(
                post(serviceCreateUrl).contentType(MediaType.APPLICATION_JSON).content(""));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_200_ok_when_object_exists() throws Exception {
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
    public void should_return_404_not_found_when_object_does_not_exists() throws Exception {
        // Given
        int containerId = 1;
        given(containerService.getContainer(containerId)).willReturn(Optional.empty());

        // When
        ResultActions response = mockMvc.perform(get(serviceDetailUrl, containerId));

        // Then
        response.andExpect(status().isNotFound());
    }
}
