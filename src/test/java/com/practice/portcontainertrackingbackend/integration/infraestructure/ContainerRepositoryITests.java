package com.practice.portcontainertrackingbackend.integration.infraestructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import com.practice.portcontainertrackingbackend.integration.AbstractionContainerBaseTests;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerRepositoryITests extends AbstractionContainerBaseTests {

    @Autowired
    private ContainerRepository containerRepository;

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
    public void should_create_object_when_save_valid_object() {
        // Given

        // When
        Container containerRetrieved = containerRepository.save(container);

        // Then
        assertThat(containerRetrieved).isNotNull();
        assertThat(containerRetrieved.getId()).isPositive();
    }

    @Test
    public void should_return_exception_when_save_invalid_object() {
        // Given an invalid container

        // When save

        // Then
        assertThatThrownBy(() -> containerRepository.save(null)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void should_retrieve_object_when_object_exist() {
        // Given
        Container containerSaved = containerRepository.save(container);

        // When
        Optional<Container> containerRetrieved = containerRepository.findById(containerSaved.getId());

        // Then
        assertThat(containerRetrieved).isNotNull();
    }

    @Test
    public void should_retrieve_empty_when_object_does_not_exist() {
        // Given
        int idNonexistentContainer = 1;

        // When
        Optional<Container> containerRetrieved = containerRepository.findById(idNonexistentContainer);

        // Then
        assertThat(containerRetrieved).isEmpty();
    }
}
