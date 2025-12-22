package com.eclark.task_aggregator_api.service;

import java.util.List;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;

public interface GoogleEventsService {
    public List<CalendarEvent> getUpcomingCalendarEvents();
    public List<CalendarEvent> getTodaysEvents();
}
