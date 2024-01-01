package com.practice.portcontainertrackingbackend.presentation.controllers;

import com.practice.portcontainertrackingbackend.application.ContainerService;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.exception.ContainerException;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.BASE_URL)
public class ContainerControllerImpl implements ContainerControllers {
    private static final Logger log = LoggerFactory.getLogger(ContainerControllerImpl.class);
    private final ContainerService containerService;

    @Autowired
    public ContainerControllerImpl(ContainerService containerService) {
        this.containerService = containerService;
    }

    @Override
    public ResponseEntity<Container> createOrder(Container container) {
        Container containerCreated = containerService.createContainer(container);
        return new ResponseEntity<>(containerCreated, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Optional<Container>> getContainer(Integer containerId) {
        Optional<Container> foundContainer = containerService.getContainer(containerId);
        return foundContainer
                .map(container -> new ResponseEntity<>(foundContainer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Container> getAllContainers() {
        return containerService.getAllContainers();
    }

    @Override
    public ResponseEntity<Container> updateContainer(Integer containerId, Container container) {
        try {
            Container containerUpdated = containerService.updateContainer(containerId, container);
            return new ResponseEntity<>(containerUpdated, HttpStatus.OK);
        } catch (ContainerException.ContainerNotFoundException e) {
            log.error("Container not found with ID: {}", containerId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request for container with ID: {}", containerId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error for update container with ID: {}", containerId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> deleteContainer(Integer containerId) {
        try {
            containerService.deleteContainerById(containerId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ContainerException.ContainerNotFoundException e) {
            log.error("Container not found with ID: {}", containerId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unexpected error for delete container with ID: {}", containerId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
