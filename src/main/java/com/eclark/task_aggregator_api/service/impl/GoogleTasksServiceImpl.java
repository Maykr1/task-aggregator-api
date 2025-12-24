package com.eclark.task_aggregator_api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.constants.TaskAggregatorApiConstants;
import com.eclark.task_aggregator_api.model.googleTasks.ListItemsWrapper;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskItemsWrapper;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
import com.eclark.task_aggregator_api.model.googleTasks.TaskRequest;
import com.eclark.task_aggregator_api.model.googleTasks.TaskResponse;
import com.eclark.task_aggregator_api.service.GoogleTasksService;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleTasksServiceImpl implements GoogleTasksService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTasksServiceImpl.class);
    private final RestClient googleRestClient;

    /**
     * Gets all lists
     * 
     * @return {@link ListItemsWrapper}
     */
    @Override
    public List<TaskList> getAllLists() {
        long start = System.currentTimeMillis();
        ListItemsWrapper listItemsWrapper = null;
        logger.info("Retrieving google tasks lists");
        
        try {
            listItemsWrapper = googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .pathSegment("tasks", "v1", "users", "@me", "lists")
                    .build()
                )
                .retrieve()
                .body(ListItemsWrapper.class);

            if (listItemsWrapper == null || listItemsWrapper.getTaskLists() == null) {
                logger.warn("Google Tasks returned no lists");
                return List.of();
            }
            
            return listItemsWrapper.getTaskLists();

        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected Error occured: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.info("[{} ms] - Finished retrieving google tasks lists", System.currentTimeMillis() - start);
        }
    }

    /**
     * Gets all tasks in a list specified by list id
     * 
     * @param String taskListId
     * 
     * @return {@link ListItemsWrapper}
     */
    @Override
    public List<Task> getTasksByListId(String taskListId) {
        long start = System.currentTimeMillis();
        TaskItemsWrapper taskItemsWrapper = null;
        logger.info("Retrieving google tasks for task list: {}" , taskListId);
        
        try {
            taskItemsWrapper = googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .pathSegment("tasks", "v1", "lists", "{tasklist}", "tasks")
                    .build(taskListId)
                )
                .retrieve()
                .body(TaskItemsWrapper.class);

            if (taskItemsWrapper == null || taskItemsWrapper.getTasks() == null) {
                logger.warn("Google Tasks returned no body/items for task list id: {}", taskListId);
                return List.of();
            }

            return TaskAggregatorApiUtil.formatTasks(taskItemsWrapper.getTasks());
            
        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected Error occured: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.info("[{} ms] - Finished retrieving google tasks for task list: {}", System.currentTimeMillis() - start, taskListId);
        }
    }

    @Override
    public TaskResponse createTask(String taskListId, TaskRequest taskRequest) {
        long start = System.currentTimeMillis();
        logger.info("Creating Google Task for task list: {}", taskListId);
        Task createdTask = null;

        try {
            createdTask = googleRestClient.post()
                .uri(uriBuilder -> uriBuilder
                    .pathSegment("tasks", "v1", "lists", "{tasklist}", "tasks")
                    .build(taskListId)
                )
                .body(taskRequest)
                .retrieve()
                .body(Task.class);

            if (createdTask == null) {
                logger.warn("Google Tasks returned no body/items after creating Task for task list id: {}", taskListId);
                return new TaskResponse();
            }

            return TaskAggregatorApiUtil.buildTaskResponse(createdTask, TaskAggregatorApiConstants.CREATED);
            
        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected error occured: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.info("[{} ms] - Created Google Task for Task List: {}", System.currentTimeMillis() - start, taskListId);
        }
    }
}
