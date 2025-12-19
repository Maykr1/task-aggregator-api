package com.eclark.task_aggregator_api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.model.ListItemsWrapper;
import com.eclark.task_aggregator_api.model.TaskItemsWrapper;
import com.eclark.task_aggregator_api.service.GoogleTasksService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleTasksServiceImpl implements GoogleTasksService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleEventsServiceImpl.class);
    private final RestClient googleRestClient;

    /**
     * Gets all lists
     * 
     * @return {@link ListItemsWrapper}
     */
    @Override
    public ListItemsWrapper getAllLists() {
        long start = System.currentTimeMillis();

        logger.info("Retrieving google tasks lists");

        ListItemsWrapper response = googleRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("www.googleapis.com")
                .pathSegment("tasks", "v1", "users", "@me", "lists")
                .build()
            )
            .retrieve()
            .body(ListItemsWrapper.class);

        logger.info("[{} ms] - Finished retrieving google tasks lists", System.currentTimeMillis() - start);
        return response;
    }

    /**
     * Gets all tasks in a list specified by list id
     * 
     * @param String taskListId
     * 
     * @return {@link ListItemsWrapper}
     */
    @Override
    public TaskItemsWrapper getTasksByListId(String taskListId) {
        long start = System.currentTimeMillis();

        logger.info("Retrieving google tasks for task list: {}" , taskListId);
        
        TaskItemsWrapper response = googleRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("www.googleapis.com")
                .pathSegment("tasks", "v1", "lists", "{tasklist}", "tasks")
                .build(taskListId)
            )
            .retrieve()
            .body(TaskItemsWrapper.class);

        logger.info("[{} ms] - Finished retrieving google tasks for task list: {}", System.currentTimeMillis() - start, taskListId);
        return response;
    }
}
