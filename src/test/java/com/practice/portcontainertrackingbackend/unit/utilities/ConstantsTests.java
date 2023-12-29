package com.practice.portcontainertrackingbackend.unit.utilities;

import static org.assertj.core.api.Assertions.*;

import com.practice.portcontainertrackingbackend.utilities.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConstantsTests {
    @Nested
    class ContainerUrls {
        @Test
        public void should_verify_apis_urls() {
            // Then
            assertThat(Constants.BASE_URL).isEqualTo("/container");
            assertThat(Constants.CREATE_CONTAINER_URL).isEqualTo("/api/v1/create");
            assertThat(Constants.DETAIL_CONTAINER_URL).isEqualTo("/api/v1/detail/{containerId}");
            assertThat(Constants.LIST_CONTAINER_URL).isEqualTo("/api/v1/containers");
        }
    }
}
