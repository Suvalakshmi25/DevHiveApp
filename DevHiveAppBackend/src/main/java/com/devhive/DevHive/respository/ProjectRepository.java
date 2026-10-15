package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOrganizationId(Long orgId);
}