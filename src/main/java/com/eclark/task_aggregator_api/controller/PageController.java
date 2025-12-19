package com.eclark.task_aggregator_api.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eclark.task_aggregator_api.entity.ListItemsWrapper;
import com.eclark.task_aggregator_api.entity.Task;
import com.eclark.task_aggregator_api.entity.TaskItemsWrapper;
import com.eclark.task_aggregator_api.entity.TaskList;
import com.eclark.task_aggregator_api.service.GoogleTasksService;

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
        ListItemsWrapper listsWrapper = googleTasksService.getAllLists();
        List<TaskList> taskLists = listsWrapper.getTaskLists();
        List<String> taskListIds = taskLists.stream()
            .map(TaskList::getId)
            .toList();

        HashMap<String, List<Task>> map = new HashMap<>();
        for (String id : taskListIds) {
            TaskItemsWrapper tasksWrapper = googleTasksService.getTasksByListId(id);
            List<Task> tasks = tasksWrapper.getTasks();

            map.put(id, tasks);
        }

        model.addAttribute("taskLists", taskLists);
        model.addAttribute("tasksMap", map);

        for (TaskList item : taskLists) {
            logger.info("Task List : {}", item.getTitle());
        }

        logger.info("Example item: {}", map.get(taskListIds.get(0)).toString());

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
