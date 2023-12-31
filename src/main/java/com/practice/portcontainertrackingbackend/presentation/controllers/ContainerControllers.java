package com.practice.portcontainertrackingbackend.presentation.controllers;

import com.practice.portcontainertrackingbackend.domain.Container;
import com.practice.portcontainertrackingbackend.utilities.Constants;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface ContainerControllers {

    @PostMapping(Constants.CREATE_CONTAINER_URL)
    ResponseEntity<Container> createOrder(@RequestBody Container container);

    @GetMapping(Constants.DETAIL_CONTAINER_URL)
    ResponseEntity<Optional<Container>> getContainer(@PathVariable Integer containerId);

    @GetMapping(Constants.LIST_CONTAINER_URL)
    List<Container> getAllContainers();

    @PutMapping(Constants.UPDATE_CONTAINER_URL)
    ResponseEntity<Container> updateContainer(@PathVariable Integer containerId, @RequestBody Container container);
}
