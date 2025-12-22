package com.eclark.task_aggregator_api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
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

    @GetMapping("/tasks/lists")
    public ResponseEntity<List<TaskList>> getAllLists() {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve Google Tasks");

        List<TaskList> response = googleTasksService.getAllLists();

        logger.info("[{} ms] - Finished getting all Google Tasks", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<List<Task>> getTasksByListId(@PathVariable("id") String taskListId) {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve Google Tasks for list: {}", taskListId);

        List<Task> response = googleTasksService.getTasksByListId(taskListId);

        logger.info("[{} ms] - Finished getting all Google Tasks for list: {}", System.currentTimeMillis() - start, taskListId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/calendars/upcoming")
    public ResponseEntity<List<CalendarEvent>> getUpcomingEvents() {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve all upcoming Google Events");

        List<CalendarEvent> response = googleEventsService.getUpcomingCalendarEvents();

        logger.info("[{} ms] - Finished getting all upcoming Google Events", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/calendars/today")
    public ResponseEntity<List<CalendarEvent>> getTodaysEvents() {
        long start = System.currentTimeMillis();
        logger.info("Starting to retrieve all upcoming Google Events");

        List<CalendarEvent> response = googleEventsService.getTodaysEvents();

        logger.info("[{} ms] - Finished getting all upcoming Google Events", System.currentTimeMillis() - start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
