package com.practice.portcontainertrackingbackend.domain.repositories;

import com.practice.portcontainertrackingbackend.domain.Container;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Integer> {

    Container save(Container container);

    Optional<Container> findById(Integer id);

    List<Container> findAll();

    void deleteById(Integer id);
}
