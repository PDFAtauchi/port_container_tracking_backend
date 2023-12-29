package com.practice.portcontainertrackingbackend.unit.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.practice.portcontainertrackingbackend.application.ContainerServiceImpl;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        return Instancio.create(Container.class);
    }

    @BeforeEach
    public void setup() {
        container = generateContainer();
    }

    @Nested
    class CreateContainer {
        @Test
        void shouldCreateValidObjectSuccessfully() {
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
        void shouldThrowExceptionForInvalidObject() {
            // Given invalid object
            given(containerRepository.save(container)).willThrow(RuntimeException.class);

            // When createContainer

            // Then
            assertThatThrownBy(() -> containerRepository.save(container)).isInstanceOf(RuntimeException.class);
            verify(containerRepository, times(1)).save(container);
        }
    }

    @Nested
    class RetrieveContainer {
        @Test
        void shouldRetrieveObjectIfExists() {
            // Given
            int containerId = 1;
            Container mockContainer = container.withId(containerId);
            given(containerRepository.findById(containerId)).willReturn(Optional.of(mockContainer));

            // When
            Optional<Container> containerRetrieved = containerService.getContainer(containerId);

            // Then
            verify(containerRepository, times(1)).findById(containerId);
            assertThat(containerRetrieved).isPresent();
            assertThat(containerRetrieved.get()).isNotNull();
            assertThat(containerRetrieved.get().getId()).isEqualTo(containerId);
        }

        @Test
        void shouldRetrieveEmptyWhenObjectDoesNotExist() {
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

    @Nested
    class ListContainer {
        @Test
        void shouldReturnContainersWhenContainersExist() {
            // Given
            Container container2 = generateContainer();
            List<Container> containers = List.of(container, container2);

            given(containerRepository.findAll()).willReturn(containers);

            // When
            List<Container> retrieveContainers = containerService.getAllContainers();

            // Then
            verify(containerRepository, times(1)).findAll();
            assertThat(retrieveContainers).hasSize(2);
        }

        @Test
        void shouldReturnNoContainersWhenNoContainersExist() {
            // Given
            given(containerRepository.findAll()).willReturn(List.of());

            // When
            List<Container> retrieveContainers = containerService.getAllContainers();

            // Then
            verify(containerRepository, times(1)).findAll();
            assertThat(retrieveContainers).isEmpty();
        }
    }
}
