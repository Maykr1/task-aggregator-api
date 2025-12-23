package com.eclark.task_aggregator_api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eclark.task_aggregator_api.controller.PageController;

@ControllerAdvice(assignableTypes = PageController.class)
public class HtmlExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(HtmlExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        logger.error("[IllegalArgumentException] - An error occured: {}", e.getMessage(), e);
        redirectAttributes.addAttribute("errorMessage", e.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        logger.error("[UnexpectedException] - An error occured: {}", e.getMessage(), e);
        redirectAttributes.addAttribute("errorMessage", e.getMessage());
        return "redirect:/";
    }
}
