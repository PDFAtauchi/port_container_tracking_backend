package com.practice.portcontainertrackingbackend.application;

import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.domain.repositories.ContainerRepository;
import com.practice.portcontainertrackingbackend.exception.ContainerException;
import java.util.List;
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

    @Override
    public List<Container> getAllContainers() {
        return containerRepository.findAll();
    }

    @Override
    public Container updateContainer(int containerId, Container container) {
        Optional<Container> retrievedContainer = containerRepository.findById(containerId);

        if (retrievedContainer.isPresent()) {
            Container actualContainer = retrievedContainer.get();

            try {
                if (container.getCode() != null) {
                    actualContainer.setCode(container.getCode());
                }
                if (container.getStatus() != null) {
                    actualContainer.setStatus(container.getStatus());
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error in arguments", e);
            }

            try {
                return containerRepository.save(actualContainer);
            } catch (Exception e) {
                throw new ContainerException.ContainerUpdateException(
                        "Error updating Container with id " + containerId, e);
            }
        }
        throw new ContainerException.ContainerNotFoundException("Container with id " + containerId + " not found");
    }

    @Override
    public void deleteContainerById(int containerId) {
        containerRepository.deleteById(containerId);
    }
}
