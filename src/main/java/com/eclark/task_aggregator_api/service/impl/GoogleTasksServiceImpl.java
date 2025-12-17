package com.eclark.task_aggregator_api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.service.GoogleTasksService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleTasksServiceImpl implements GoogleTasksService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleEventsServiceImpl.class);
    private final RestClient googleRestClient;

    // Lists tasks lists
    @Override
    public String getGoogleTasks() {
        long start = System.currentTimeMillis();

        logger.info("Retrieving google tasks lists");

        String response = googleRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("www.googleapis.com")
                .pathSegment("users", "@me", "lists")
                .build()
            )
            .retrieve()
            .body(String.class);

        logger.info("[{} ms] - Finished retrieving google tasks lists", System.currentTimeMillis() - start);
        return response;
    }

    // List tasks in a list
    @Override
    public String getGoogleTasks2(String taskListId) {
        long start = System.currentTimeMillis();

        logger.info("Retrieving google tasks for task list: {}" , taskListId);
        
        String response = googleRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("www.googleapis.com")
                .pathSegment("lists", "{tasklist}", "tasks")
                .build(taskListId)
            )
            .retrieve()
            .body(String.class);

        logger.info("[{} ms] - Finished retrieving google tasks for task list: {}", System.currentTimeMillis() - start, taskListId);
        return response;
    }
}
