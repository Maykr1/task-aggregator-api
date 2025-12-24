package com.eclark.task_aggregator_api.service;

import java.util.List;

import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
import com.eclark.task_aggregator_api.model.googleTasks.TaskRequest;
import com.eclark.task_aggregator_api.model.googleTasks.TaskResponse;

public interface GoogleTasksService {
    public List<TaskList> getAllLists();
    public List<Task> getTasksByListId(String taskListId);
    public TaskResponse createTask(String taskListId, TaskRequest taskRequest);
}
