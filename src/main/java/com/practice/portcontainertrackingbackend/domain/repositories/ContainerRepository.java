package com.practice.portcontainertrackingbackend.domain.repositories;

import com.practice.portcontainertrackingbackend.domain.Container;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Integer> {

    Container save(Container container);

    Optional<Container> findById(Integer id);
}
