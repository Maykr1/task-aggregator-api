package com.eclark.task_aggregator_api.service;

import java.util.List;

import com.eclark.task_aggregator_api.model.Task;
import com.eclark.task_aggregator_api.model.TaskList;

public interface GoogleTasksService {
    public List<TaskList> getAllLists();
    public List<Task> getTasksByListId(String taskListId);
}
