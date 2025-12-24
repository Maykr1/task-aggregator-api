package com.eclark.task_aggregator_api.service;

import java.util.List;

import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEventRequest;
import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEventResponse;

public interface GoogleEventsService {
    public List<CalendarEvent> getUpcomingCalendarEvents();
    public List<CalendarEvent> getTodaysEvents();
    public CalendarEventResponse createCalendarEvent(CalendarEventRequest calendarEventRequest);
}
