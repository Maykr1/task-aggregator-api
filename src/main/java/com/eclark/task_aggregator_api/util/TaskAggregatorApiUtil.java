package com.eclark.task_aggregator_api.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.eclark.task_aggregator_api.model.Task;
import com.eclark.task_aggregator_api.model.TaskList;

public class TaskAggregatorApiUtil {
    public static List<String> getTaskListIds(List<TaskList> taskLists) {
        if (taskLists == null) {
            throw new IllegalArgumentException("taskLists are null");
        }

        return taskLists.stream()
            .map(TaskList::getId)
            .toList();
    }

    public static List<Task> formatTasks(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("tasks are null");
        }

        List<Task> clone = new ArrayList<>();

        // Go through each task (parent)
        for (Task parent : tasks) {
            ArrayList<Task> children = new ArrayList<>();

            // If a child is related to a parent, add it to a list
            for (Task child: tasks) {
                if (child.getParent() != null && parent.getId().equals(child.getParent())) {
                    children.add(child);
                    continue;
                }
            }

            // Add this list of children to parent
            parent.setChildren(children.reversed());

            // If the current task is a parent (aka has no children) add to new list
            if (parent.getParent() == null) {
                clone.add(parent);
            }
        }

        return clone;
    }

    public static String formatTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }

        OffsetDateTime odt = OffsetDateTime.parse(dateTime);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return odt.format(fmt);
    }
}
