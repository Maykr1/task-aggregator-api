package com.eclark.task_aggregator_api.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        List<Task> clone = new ArrayList<>();

        if (tasks == null) {
            return clone;            
        }

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

    // Redundant method, but I am setting up myself for future changes here
    public static List<CalendarEvent> formatEvents(List<CalendarEvent> calendarEvents) {
        List<CalendarEvent> clone = new ArrayList<>();

        if (calendarEvents == null) {
            return clone;
        }

        for (CalendarEvent event : calendarEvents) {
            clone.add(event);
        }

        return clone;
    }

    public static String formatTaskTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }

        OffsetDateTime odt = OffsetDateTime.parse(dateTime);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return odt.format(fmt);
    }

    public static String formatEventTimeWithZone(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }

        if (dateTime.contains("T")) {
            // 2025-12-22T10:00:00-05:00 example
            
            OffsetDateTime odt = OffsetDateTime.parse(dateTime);
            ZonedDateTime zdt = odt.atZoneSameInstant(ZoneId.of("America/New_York"));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
            
            return zdt.format(fmt);
        } else {
            // 2026-05-07 example

            LocalDate odt = LocalDate.parse(dateTime);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");

            log.info(odt.format(fmt));
            return odt.format(fmt);
        }
    }
}
