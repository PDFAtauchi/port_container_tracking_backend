package com.practice.portcontainertrackingbackend.unit.domain;

import static org.assertj.core.api.Assertions.*;

import com.practice.portcontainertrackingbackend.domain.Container;
import org.junit.jupiter.api.Test;

public class ContainerTests {

    @Test
    public void should_create_container_object_when_container_object_is_created() {
        // Given
        int id = 1;
        String code = "ABC";
        String status = "UNLOADING";

        // When
        Container container =
                Container.builder().id(id).code(code).status(status).build();

        // Then
        assertThat(container).isNotNull();
    }
}
