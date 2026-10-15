package com.devhive.DevHive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devhive.DevHive.dto.task.AssignTaskRequest;
import com.devhive.DevHive.dto.task.CreateTaskRequest;
import com.devhive.DevHive.dto.task.UpdateStatusRequest;
import com.devhive.DevHive.dto.task.UpdateTaskRequest;
import com.devhive.DevHive.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }
    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER','VIEWER')")
    public ResponseEntity<?> getTasks(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                        @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignTask(@PathVariable Long id,
                                        @RequestBody AssignTaskRequest request) {
        return ResponseEntity.ok(taskService.assignTask(id, request));
    }
}