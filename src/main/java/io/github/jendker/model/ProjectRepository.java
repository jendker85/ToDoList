package io.github.jendker.model;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    public List<Project> findAll();
    public Optional<Project> findById(Integer id);
    public Project save (Project entity);
}
