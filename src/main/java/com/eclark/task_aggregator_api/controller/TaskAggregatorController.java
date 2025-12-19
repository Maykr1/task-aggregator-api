package com.eclark.task_aggregator_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclark.task_aggregator_api.entity.ListItemsWrapper;
import com.eclark.task_aggregator_api.entity.TaskItemsWrapper;
import com.eclark.task_aggregator_api.service.GoogleEventsService;
import com.eclark.task_aggregator_api.service.GoogleTasksService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskAggregatorController {
    private final GoogleTasksService googleTasksService;
    private final GoogleEventsService googleEventsService;
    private static final Logger logger = LoggerFactory.getLogger(TaskAggregatorController.class);

    @GetMapping("/tasks")
    public ResponseEntity<ListItemsWrapper> getAllLists() {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve Google Tasks");

        ListItemsWrapper response = googleTasksService.getAllLists();

        logger.info("[{} ms] - Finished getting all Google Tasks", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskItemsWrapper> getTasksByListId(@PathVariable("id") String taskListId) {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve Google Tasks for list: {}", taskListId);

        TaskItemsWrapper response = googleTasksService.getTasksByListId(taskListId);

        logger.info("[{} ms] - Finished getting all Google Tasks for list: {}", System.currentTimeMillis() - start, taskListId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getTodaysEvents() {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve all Google Events for today");

        Object response = googleEventsService.getTodaysEvent();

        logger.info("[{} ms] - Finished getting all Google Events for today", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
