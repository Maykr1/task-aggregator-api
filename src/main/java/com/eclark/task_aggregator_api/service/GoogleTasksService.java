package com.eclark.task_aggregator_api.service;

import com.eclark.task_aggregator_api.model.ListItemsWrapper;
import com.eclark.task_aggregator_api.model.TaskItemsWrapper;

public interface GoogleTasksService {
    public ListItemsWrapper getAllLists();
    public TaskItemsWrapper getTasksByListId(String taskListId);
}
