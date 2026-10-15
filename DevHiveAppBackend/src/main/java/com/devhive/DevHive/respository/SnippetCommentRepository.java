package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.SnippetComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetCommentRepository extends JpaRepository<SnippetComment, Long> {

    List<SnippetComment> findBySnippetId(Long snippetId);
}