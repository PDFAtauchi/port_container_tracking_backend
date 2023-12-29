package com.practice.portcontainertrackingbackend.unit.domain.repositories;

import static org.assertj.core.api.Assertions.*;

import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerRepositoryTests {

    @Autowired
    private ContainerRepository containerRepository;

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
        void shouldPersistObjectSuccessfullyWhenCreateValidObject() {
            // Given

            // When
            Container containerRetrieved = containerRepository.save(container);

            // Then
            assertThat(containerRetrieved).isNotNull();
            assertThat(containerRetrieved.getId()).isPositive();
        }

        @Test
        void shouldThrowExceptionWhenSaveInvalidObject() {
            // Given an invalid container

            // When save

            // Then
            assertThatThrownBy(() -> containerRepository.save(null)).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    class RetrieveContainer {
        @Test
        void shouldRetrieveObjectWhenObjectExists() {
            // Given
            Container containerSaved = containerRepository.save(container);

            // When
            Optional<Container> containerRetrieved = containerRepository.findById(containerSaved.getId());

            // Then
            assertThat(containerRetrieved).isNotNull();
        }

        @Test
        void shouldRetrieveEmptyWhenObjectDoesNotExist() {
            // Given
            int idNonexistentContainer = 1;

            // When
            Optional<Container> containerRetrieved = containerRepository.findById(idNonexistentContainer);

            // Then
            assertThat(containerRetrieved).isEmpty();
        }
    }

    @Nested
    class ListContainer {
        @Test
        void shouldReturnContainersWhenExist() {
            // Given
            containerRepository.save(container);
            containerRepository.save(generateContainer());
            containerRepository.save(generateContainer());

            // When
            List<Container> containers = containerRepository.findAll();

            // Then
            assertThat(containers).hasSize(3);
            assertThat(containers.get(0).getId()).isPositive();
            assertThat(containers.get(1).getId()).isPositive();
            assertThat(containers.get(2).getId()).isPositive();
        }

        @Test
        void shouldReturnNoContainersIfNoneExist() {
            // Given no containers in db

            // When
            List<Container> containers = containerRepository.findAll();

            // Then
            assertThat(containers).isEmpty();
        }
    }
}
