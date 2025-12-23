package com.eclark.task_aggregator_api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
import com.eclark.task_aggregator_api.service.GoogleEventsService;
import com.eclark.task_aggregator_api.service.GoogleTasksService;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final GoogleTasksService googleTasksService;
    private final GoogleEventsService googleEventsService;

    @GetMapping("/")
    public String home(Model model) {
        // Retrieve Lists
        List<TaskList> taskLists = googleTasksService.getAllLists();

        // Create HashMap of:
        // <List Id, List<Tasks>>
        Map<String, List<Task>> map = new HashMap<>();
        for (String id : TaskAggregatorApiUtil.getTaskListIds(taskLists)) {
            List<Task> tasks = googleTasksService.getTasksByListId(id);

            map.put(id, tasks);
        }

        // Retrieve Todays & Upcoming Calendar Events
        List<CalendarEvent> todaysCalendarEvents = googleEventsService.getTodaysEvents();
        List<CalendarEvent> upcomingCalendarEvents = googleEventsService.getUpcomingCalendarEvents();
        
        model.addAttribute("taskLists", taskLists);
        model.addAttribute("tasksMap", map);
        model.addAttribute("todaysCalendarEvents", todaysCalendarEvents);
        model.addAttribute("upcomingCalendarEvents", upcomingCalendarEvents);

        return "index";
    }

    @GetMapping("/back-to-portfolio")
    public String backToPortfolio() {
        return "redirect://ethansclark.com";
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }

        return "error";
    }
}
