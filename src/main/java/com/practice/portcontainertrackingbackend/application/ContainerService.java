package com.practice.portcontainertrackingbackend.application;

import com.practice.portcontainertrackingbackend.domain.Container;
import java.util.Optional;

public interface ContainerService {
    Container createContainer(Container container);

    Optional<Container> getContainer(int containerId);
}
