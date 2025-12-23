package com.eclark.task_aggregator_api.service;

import java.util.List;

import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;

public interface GoogleTasksService {
    public List<TaskList> getAllLists();
    public List<Task> getTasksByListId(String taskListId);
}
