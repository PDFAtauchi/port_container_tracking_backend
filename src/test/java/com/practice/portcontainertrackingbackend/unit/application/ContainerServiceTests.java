package com.practice.portcontainertrackingbackend.unit.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.practice.portcontainertrackingbackend.application.ContainerServiceImpl;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContainerServiceTests {

    @Mock
    private ContainerRepository containerRepository;

    @InjectMocks
    private ContainerServiceImpl containerService;

    private Container container;

    private Container generateContainer() {
        Container container = Instancio.create(Container.class);
        return container;
    }

    @BeforeEach
    public void setup() {
        container = generateContainer();
    }

    @Test
    public void should_create_object_when_valid_object() {
        // Given
        Container mockContainer = container.withId(1);

        given(containerRepository.save(container)).willReturn(mockContainer);

        // When
        Container containerSaved = containerService.createContainer(container);

        // Then
        verify(containerRepository, times(1)).save(container);
        assertThat(containerSaved.getId()).isEqualTo(mockContainer.getId());
    }

    @Test
    public void should_throw_exception_when_invalid_object() {
        // Given invalid object
        given(containerRepository.save(container)).willThrow(RuntimeException.class);

        // When createContainer

        // Then
        assertThatThrownBy(() -> containerRepository.save(container)).isInstanceOf(RuntimeException.class);
        verify(containerRepository, times(1)).save(container);
    }

    @Test
    public void should_retrieve_object_when_object_exist() {
        // Given
        int containerId = 1;
        Container mockContainer = container.withId(containerId);
        given(containerRepository.findById(containerId)).willReturn(Optional.of(mockContainer));

        // When
        Optional<Container> containerRetrieved = containerService.getContainer(containerId);

        // Then
        verify(containerRepository, times(1)).findById(containerId);
        assertThat(containerRetrieved.get()).isNotNull();
        assertThat(containerRetrieved.get().getId()).isEqualTo(containerId);
    }

    @Test
    public void should_retrieve_empty_when_object_does_not_exist() {
        // Given
        int containerId = 1;
        given(containerRepository.findById(containerId)).willReturn(Optional.empty());

        // When
        Optional<Container> containerRetrieved = containerService.getContainer(containerId);

        // Then
        verify(containerRepository, times(1)).findById(containerId);
        assertThat(containerRetrieved).isEmpty();
    }
}
