package com.eclark.task_aggregator_api.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.service.GoogleEventsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleEventsServiceImpl implements GoogleEventsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleEventsServiceImpl.class);
    private final RestClient googleRestClient;

    @Override
    public String getTodaysEvent() {
        long start = System.currentTimeMillis();
        String calendarId   = "primary";
        Instant beginning   = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end         = LocalDate.now(ZoneId.systemDefault()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        logger.info("Retrieving today's Google Calendar Events");

        String response = googleRestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/calendar/v3/calnedars/{calendarId}/events")
                .queryParam("timeMin", beginning.toString())
                .queryParam("timeMax", end.toString())
                .queryParam("singleEvents", true)
                .queryParam("orderBy", "startTime")
                .build(calendarId)
            )
            .retrieve()
            .body(String.class);

        logger.info("[{} ms] - Finished retrieving today's Google Calendar Events", System.currentTimeMillis() - start);
        return response;
    }
}
