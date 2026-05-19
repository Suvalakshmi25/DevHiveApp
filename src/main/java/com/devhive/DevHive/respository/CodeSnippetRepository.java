package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.CodeSnippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSnippetRepository extends JpaRepository<CodeSnippet, Long> {

    List<CodeSnippet> findByProjectId(Long projectId);

    List<CodeSnippet> findByTitleContainingIgnoreCase(String title);
}