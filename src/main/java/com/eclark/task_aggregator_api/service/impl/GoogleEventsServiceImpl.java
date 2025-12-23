package com.eclark.task_aggregator_api.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.constants.TaskAggregatorApiConstants;
import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEventsWrapper;
import com.eclark.task_aggregator_api.service.GoogleEventsService;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleEventsServiceImpl implements GoogleEventsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleEventsServiceImpl.class);
    private final RestClient googleRestClient;

    @Override
    public List<CalendarEvent> getUpcomingCalendarEvents() {
        long start          = System.currentTimeMillis();
        Integer maxResults  = 10;
        Instant now         = LocalDate.now(ZoneId.systemDefault()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        CalendarEventsWrapper response = null;

        logger.info("Retrieving upcoming {} Google Calendar Events starting on: {}", maxResults, now);

        try {
            response = googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .pathSegment("calendar", "v3", "calendars", "{calendarId}", "events")
                    .queryParam("maxResults", maxResults)
                    .queryParam("orderBy", TaskAggregatorApiConstants.ORDER_BY_START_TIME)
                    .queryParam("singleEvents", true)
                    .queryParam("timeMin", now.toString())
                    .build(TaskAggregatorApiConstants.PRIMARY_CALENDAR_NAME)
                )
                .retrieve()
                .body(CalendarEventsWrapper.class);

            if (response == null || response.getCalendarEvents() == null) {
                logger.warn("Google Calendar returned no body/items for upcoming events");
                return List.of();
            }

            return TaskAggregatorApiUtil.formatEvents(response.getCalendarEvents());
            
        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected Error occured: {}", e.getMessage(), e);
            return List.of();
        } finally {
            logger.info("[{} ms] - Finished retrieving upcoming Google Calendar Events", System.currentTimeMillis() - start);
        }
    }

    @Override
    public List<CalendarEvent> getTodaysEvents() {
        long start          = System.currentTimeMillis();
        Instant beginning   = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end         = LocalDate.now(ZoneId.systemDefault()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        CalendarEventsWrapper response = null;

        logger.info("Retrieving today's Google Calendar Events between: {} and {}", beginning, end);

        try {
            response = googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .pathSegment("calendar", "v3", "calendars", "{calendarId}", "events")
                    .queryParam("timeMin", beginning.toString())
                    .queryParam("timeMax", end.toString())
                    .queryParam("singleEvents", true)
                    .queryParam("orderBy", TaskAggregatorApiConstants.ORDER_BY_START_TIME)
                    .build(TaskAggregatorApiConstants.PRIMARY_CALENDAR_NAME)
                )
                .retrieve()
                .body(CalendarEventsWrapper.class);
            
            if (response == null || response.getCalendarEvents() == null) {
                logger.warn("Google Calendar returned no body/items for upcoming events");
                return List.of();
            }

            return TaskAggregatorApiUtil.formatEvents(response.getCalendarEvents());

        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected Error occured: {}", e.getMessage(), e);
            return List.of();
        } finally {
            logger.info("[{} ms] - Finished retrieving today's Google Calendar Events", System.currentTimeMillis() - start);
        }
    }
}
