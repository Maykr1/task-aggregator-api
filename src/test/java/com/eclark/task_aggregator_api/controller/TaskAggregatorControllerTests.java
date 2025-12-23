package com.eclark.task_aggregator_api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
import com.eclark.task_aggregator_api.service.GoogleEventsService;
import com.eclark.task_aggregator_api.service.GoogleTasksService;

@WebMvcTest(TaskAggregatorController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskAggregatorControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoogleTasksService googleTasksService;

    @MockitoBean 
    private GoogleEventsService googleEventsService;

    TaskList taskList1;
    TaskList taskList2;
    Task task1;
    Task task2;
    CalendarEvent calendarEvent1;
    CalendarEvent calendarEvent2;

    @BeforeEach
    public void setup() {
        taskList1 = new TaskList();
        taskList1.setId("A");
        taskList1.setTitle("Inbox");
        taskList2 = new TaskList();
        taskList2.setId("B");
        taskList2.setTitle("School");

        task1 = new Task();
        task1.setId("p1");
        task1.setTitle("Parent");
        task2 = new Task();
        task2.setId("c1");
        task2.setTitle("Child");
        task2.setParent("p1");

        calendarEvent1 = new CalendarEvent();
        calendarEvent1.setId("1");
        calendarEvent1.setTitle("Doctor's Appointment");
        calendarEvent2 = new CalendarEvent();
        calendarEvent2.setId("2");
        calendarEvent2.setTitle("Grocery Store");
    }

    @Test
    void getAllLists() throws Exception {
        when(googleTasksService.getAllLists()).thenReturn(List.of(taskList1, taskList2));
        
        mockMvc.perform(get("/api/tasks/lists")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("A"))
            .andExpect(jsonPath("$[1].id").value("B"));
    
        verify(googleTasksService).getAllLists();
    }

    @Test
    void getTasksByListId() throws Exception {
        when(googleTasksService.getTasksByListId("A")).thenReturn(List.of(task1, task2));

        mockMvc.perform(get("/api/tasks/{id}", "A")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("p1"))
            .andExpect(jsonPath("$[1].id").value("c1"));

        verify(googleTasksService).getTasksByListId("A");
        verifyNoInteractions(googleEventsService);
    }

    @Test
    void getUpcomingGoogleEvents() throws Exception {
        when(googleEventsService.getUpcomingCalendarEvents()).thenReturn(List.of(calendarEvent1, calendarEvent2));

        mockMvc.perform(get("/api/calendars/upcoming")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].summary").value("Doctor's Appointment"))
            .andExpect(jsonPath("$[1].summary").value("Grocery Store"));

        verify(googleEventsService).getUpcomingCalendarEvents();
    }

    @Test
    void getTodaysGoogleEvents() throws Exception {
        when(googleEventsService.getTodaysEvents()).thenReturn(List.of(calendarEvent1, calendarEvent2));

        mockMvc.perform(get("/api/calendars/today")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].summary").value("Doctor's Appointment"))
            .andExpect(jsonPath("$[1].summary").value("Grocery Store"));

        verify(googleEventsService).getTodaysEvents();
    }
}
