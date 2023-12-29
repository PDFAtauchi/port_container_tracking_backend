package com.practice.portcontainertrackingbackend.presentation.controllers;

import com.practice.portcontainertrackingbackend.application.ContainerService;
import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.BASE_URL)
public class ContainerControllerImpl implements ContainerControllers {

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
}
