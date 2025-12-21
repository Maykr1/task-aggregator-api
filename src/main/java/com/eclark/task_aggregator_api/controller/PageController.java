package com.eclark.task_aggregator_api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eclark.task_aggregator_api.model.Task;
import com.eclark.task_aggregator_api.model.TaskList;
import com.eclark.task_aggregator_api.service.GoogleTasksService;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    private final GoogleTasksService googleTasksService;

    @GetMapping("/")
    public String home(Model model) {
        // Retrieve Lists
        List<TaskList> taskLists = googleTasksService.getAllLists();
        List<String> taskListIds = TaskAggregatorApiUtil.getTaskListIds(taskLists);

        // Create HashMap of:
        // <List Id, List<Tasks>>
        Map<String, List<Task>> map = new HashMap<>();
        for (String id : taskListIds) {
            List<Task> tasks = googleTasksService.getTasksByListId(id);

            map.put(id, tasks);
        }

        model.addAttribute("taskLists", taskLists);
        model.addAttribute("tasksMap", map);
        
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
