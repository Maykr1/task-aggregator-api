package com.eclark.task_aggregator_api.service;

import com.eclark.task_aggregator_api.entity.ListItemsWrapper;
import com.eclark.task_aggregator_api.entity.TaskItemsWrapper;

public interface GoogleTasksService {
    public ListItemsWrapper getAllLists();
    public TaskItemsWrapper getTasksByListId(String taskListId);
}
