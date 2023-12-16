package com.practice.portcontainertrackingbackend.application;

import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerServiceImpl implements ContainerService {

    private ContainerRepository containerRepository;

    @Autowired
    public ContainerServiceImpl(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    @Override
    public Container createContainer(Container container) {
        return containerRepository.save(container);
    }

    @Override
    public Optional<Container> getContainer(int containerId) {
        return containerRepository.findById(containerId);
    }
}
