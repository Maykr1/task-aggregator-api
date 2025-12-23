package com.eclark.task_aggregator_api.util;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;

public class TaskAggregatorApiUtilTests {
    @Test
    void getTaskListIds_mapIds() {
        TaskList task1 = new TaskList();
        task1.setId("A");
        TaskList task2 = new TaskList();
        task2.setId("B");    
        TaskList task3 = new TaskList();
        task3.setId("C");

        List<String> ids = TaskAggregatorApiUtil.getTaskListIds(List.of(task1, task2, task3));

        assertEquals(List.of("A", "B", "C"), ids);
    }

    @Test
    void getTaskListIds_empty() {
        List<String> ids = TaskAggregatorApiUtil.getTaskListIds(List.of());
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    void getTaskListsIds_null() {
        assertThrows(
            IllegalArgumentException.class,
            () -> TaskAggregatorApiUtil.getTaskListIds(null)            
        );
    }

    @Test
    void formatTaskTime_null() {
        assertEquals("", TaskAggregatorApiUtil.formatTaskTime(null));
    }

    @Test
    void formatTaskTime_empty() {
        assertEquals("", TaskAggregatorApiUtil.formatTaskTime(""));
    }

    @Test
    void formatTaskTime_formatsCorrectly() {
        String input = "2025-12-20T13:45:00Z";
        String output = TaskAggregatorApiUtil.formatTaskTime(input);

        assertEquals("Dec 20, 2025", output);
    }

    @Test
    void formatTaskTime_invalid() {
        assertThrows(
            DateTimeException.class,
            () -> TaskAggregatorApiUtil.formatTaskTime("not-a-date")
        );
    }

    @Test
    void formatTasks_returnOnlyParents() {
        Task p1 = new Task();
        p1.setId("p1");
        Task p2 = new Task();
        p2.setId("p2");
        Task c1 = new Task();
        c1.setId("c1");
        c1.setParent("p1");
        Task c2 = new Task();
        c2.setId("c2");
        c2.setParent("p2");

        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of(p1, p2, c1, c2));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getParent() == null));
        assertEquals(List.of("p1", "p2"), result.stream().map(Task::getId).toList());
    }

    @Test
    void formatTasks_attachesChildren() {
        Task p1 = new Task();
        p1.setId("p1");
        Task p2 = new Task();
        p2.setId("p2");
        Task c1 = new Task();
        c1.setId("c1");
        c1.setParent("p1");
        Task c2 = new Task();
        c2.setId("c2");
        c2.setParent("p1");
        Task c3 = new Task();
        c3.setId("c3");
        c3.setParent("p2");

        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of(p1, p2, c1, c2, c3));

        Task rp1 = result.stream().filter(t -> "p1".equals(t.getId())).findFirst().orElseThrow();
        Task rp2 = result.stream().filter(t -> "p2".equals(t.getId())).findFirst().orElseThrow();

        assertNotNull(rp1.getChildren());
        assertNotNull(rp2.getChildren());

        assertEquals(List.of("c1", "c2"), rp1.getChildren().stream().map(Task::getId).sorted().toList());
        assertEquals(List.of("c3"), rp2.getChildren().stream().map(Task::getId).toList());
    }

    @Test
    void formatTasks_childrenReversed() {
        Task p1 = new Task();
        p1.setId("p1");
        Task c1 = new Task();
        c1.setId("c1");
        c1.setParent("p1");
        Task c2 = new Task();
        c2.setId("c2");
        c2.setParent("p1");
        Task c3 = new Task();
        c3.setId("c3");
        c3.setParent("p1");

        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of(p1, c1, c2, c3));

        Task rp1 = result.get(0);
        List<String> childIds = rp1.getChildren().stream().map(Task::getId).toList();

        assertEquals(List.of("c3", "c2", "c1"), childIds);
    }

    @Test
    void formatTasks_parentNoChildren_emptyChildrenList() {
        Task p1 = new Task();
        p1.setId("p1");

        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of(p1));

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getChildren());
        assertTrue(result.get(0).getChildren().isEmpty());
    }

    @Test
    void formatTasks_orphanChildNotReturned() {
        Task orphan = new Task();
        orphan.setId("c1");
        orphan.setParent("missing-parent");

        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of(orphan));

        assertTrue(result.isEmpty(), "Only tasks with parent == null should be returned");
    }

    @Test
    void formatTasks_empty() {
        List<Task> result = TaskAggregatorApiUtil.formatTasks(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void formatTasks_null() {
        assertEquals(TaskAggregatorApiUtil.formatEvents(null), new ArrayList<Task>());
    }

    @Test
    void formatEvents_nullInput_returnsEmptyList() {
        List<CalendarEvent> result = TaskAggregatorApiUtil.formatEvents(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void formatEvents_emptyList_returnsEmptyList() {
        List<CalendarEvent> result = TaskAggregatorApiUtil.formatEvents(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void formatEvents_returnsShallowCopy() {
        CalendarEvent e1 = new CalendarEvent();
        e1.setId("1");

        CalendarEvent e2 = new CalendarEvent();
        e2.setId("2");

        List<CalendarEvent> original = List.of(e1, e2);
        List<CalendarEvent> result = TaskAggregatorApiUtil.formatEvents(original);

        assertEquals(2, result.size());
        assertSame(e1, result.get(0));
        assertSame(e2, result.get(1));
        assertNotSame(original, result);
    }

    @Test
    void formatEventTimeWithZone_nullInput_returnsEmptyString() {
        assertEquals("", TaskAggregatorApiUtil.formatEventTimeWithZone(null));
    }

    @Test
    void formatEventTimeWithZone_emptyInput_returnsEmptyString() {
        assertEquals("", TaskAggregatorApiUtil.formatEventTimeWithZone(""));
    }

    @Test
    void formatEventTimeWithZone_isoDateTime_convertsToEasternTime() {
        String input = "2025-12-22T10:00:00-05:00";
        String result = TaskAggregatorApiUtil.formatEventTimeWithZone(input);
        OffsetDateTime odt = OffsetDateTime.parse(input);
        ZonedDateTime zdt = odt.atZoneSameInstant(ZoneId.of("America/New_York"));
        String expected = zdt.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));

        assertEquals(expected, result);
    }

    @Test
    void formatEventTimeWithZone_localDate_formatsDateOnly() {
        String input = "2026-05-07";
        String result = TaskAggregatorApiUtil.formatEventTimeWithZone(input);

        assertEquals("May 7, 2026", result);
    }

    @Test
    void formatEventTimeWithZone_isoDateTime_midnightEdgeCase() {
        String input = "2025-01-01T00:00:00-05:00";
        String result = TaskAggregatorApiUtil.formatEventTimeWithZone(input);

        assertEquals("Jan 1, 2025 12:00 AM", result);
    }
}
