package com.practice.portcontainertrackingbackend.unit.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.practice.portcontainertrackingbackend.application.ContainerServiceImpl;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import com.practice.portcontainertrackingbackend.exception.ContainerException;
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

        @Test
        void shouldThrowExceptionForInvalidObjectWithNullFields() {
            // Given invalid object
            container.setCode(null);
            container.setStatus(null);

            // When And Then
            assertThatThrownBy(() -> containerService.createContainer(container))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(containerRepository, times(0)).save(container);
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

    @Nested
    class UpdateContainer {
        @Test
        void shouldUpdateWhenObjectExistAndValid() {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();

            given(containerRepository.findById(containerId)).willReturn(Optional.of(container));
            given(containerRepository.save(any(Container.class))).willReturn(container);

            // When
            Container updatedContainer = containerService.updateContainer(containerId, newContainer);

            // Then
            verify(containerRepository, times(1)).findById(containerId);
            verify(containerRepository, times(1)).save(any(Container.class));
            assertThat(updatedContainer).isNotNull();
            assertThat(updatedContainer.getCode()).isEqualTo(newContainer.getCode());
            assertThat(updatedContainer.getStatus()).isEqualTo(newContainer.getStatus());
        }

        @Test
        void shouldThrowExceptionWhenUpdateNoExistingContainer() {
            // Given
            int containerId = 1;
            Container newContainer = generateContainer();
            given(containerRepository.findById(containerId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> containerService.updateContainer(containerId, newContainer))
                    .isInstanceOf(ContainerException.ContainerNotFoundException.class);
        }

        @Test
        void shouldThrowExceptionWhenErrorSavingContainer() {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();

            given(containerRepository.findById(containerId)).willReturn(Optional.of(container));
            given(containerRepository.save(any(Container.class))).willThrow(RuntimeException.class);

            // When & Then
            assertThatThrownBy(() -> containerService.updateContainer(containerId, newContainer))
                    .isInstanceOf(RuntimeException.class);
            verify(containerRepository, times(1)).findById(containerId);
            verify(containerRepository, times(1)).save(any(Container.class));
        }

        @Test
        void shouldThrowExceptionWhenInvalidStatusInContainerUpdate() {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();

            Container containerToUpdate = mock(Container.class);
            doThrow(IllegalArgumentException.class).when(containerToUpdate).setStatus(any());
            given(containerRepository.findById(containerId)).willReturn(Optional.of(containerToUpdate));

            // When & Then
            assertThatThrownBy(() -> containerService.updateContainer(containerId, newContainer))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(containerRepository, times(1)).findById(containerId);
        }

        @Test
        void shouldNotUpdateWhenNullAttributes() {
            // Given
            int containerId = 1;
            container.setId(containerId);
            Container newContainer = generateContainer();
            newContainer.setCode(null);
            newContainer.setStatus(null);

            given(containerRepository.findById(containerId)).willReturn(Optional.of(container));
            given(containerRepository.save(any(Container.class))).willReturn(container);

            // When
            Container updatedContainer = containerService.updateContainer(containerId, newContainer);

            // Then
            verify(containerRepository, times(1)).findById(containerId);
            verify(containerRepository, times(1)).save(any(Container.class));
            assertThat(updatedContainer).isNotNull();
            assertThat(updatedContainer.getCode()).isNotNull();
            assertThat(updatedContainer.getStatus()).isNotNull();
        }
    }

    @Nested
    class DeleteContainer {
        @Test
        void shouldDeleteObjectWhenExist() {
            // Given
            int containerId = 1;
            container.setId(containerId);
            given(containerRepository.findById(containerId)).willReturn(Optional.of(container));
            doNothing().when(containerRepository).deleteById(containerId);

            // When
            containerService.deleteContainerById(container.getId());

            // Then
            verify(containerRepository, times(1)).deleteById(containerId);
        }

        @Test
        void shouldNotDeleteObjectWhenNotExist() {
            // Given
            int containerId = 1;

            // When & Then
            assertThatThrownBy(() -> containerService.deleteContainerById(containerId))
                    .isInstanceOf(ContainerException.ContainerNotFoundException.class);
            verify(containerRepository, times(0)).deleteById(containerId);
        }
    }
}
