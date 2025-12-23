package com.eclark.task_aggregator_api.model.googleCalendar;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * Wrapper used for retrieving Google Calendar Events by Calendar Id API
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarEventsWrapper {
    private String summary;
    private String updated;
    private String timeZone;

    @JsonProperty("items")
    private List<CalendarEvent> calendarEvents;
}
