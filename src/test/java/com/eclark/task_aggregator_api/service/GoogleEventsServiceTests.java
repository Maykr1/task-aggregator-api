package com.eclark.task_aggregator_api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.constants.TaskAggregatorApiConstants;
import com.eclark.task_aggregator_api.model.googleCalendar.CalendarEvent;
import com.eclark.task_aggregator_api.service.impl.GoogleEventsServiceImpl;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class GoogleEventsServiceTests {
    private MockWebServer mockWebServer;
    private GoogleEventsServiceImpl googleEventsServiceImpl;

    @BeforeEach
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        RestClient restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();

        googleEventsServiceImpl = new GoogleEventsServiceImpl(restClient);
    }

    @AfterEach
    public void teardown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getUpcomingCalendarEvents() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                    {
                        "items": [
                            { "id": "A", "title": "Doctor's Appointment" },
                            { "id": "B", "title": "Grocery Store" }
                        ]
                    }
                    """));
        
        CalendarEvent formatted1 = new CalendarEvent();
        formatted1.setId("FE1");

        List<CalendarEvent> formatted = List.of(formatted1);
        formatted1.setTitle("Formatted 1");

        try (MockedStatic<TaskAggregatorApiUtil> util = mockStatic(TaskAggregatorApiUtil.class)) {
            util.when(() -> TaskAggregatorApiUtil.formatEvents(anyList())).thenReturn(formatted);

            List<CalendarEvent> output = googleEventsServiceImpl.getUpcomingCalendarEvents();
            RecordedRequest request = mockWebServer.takeRequest();
            HttpUrl url = request.getRequestUrl();
            String encodedCalendarId = URLEncoder.encode(
                TaskAggregatorApiConstants.PRIMARY_CALENDAR_NAME,
                StandardCharsets.UTF_8
            );

            assertNotNull(output);
            assertEquals("GET", request.getMethod());
            assertNotNull(url);
            assertEquals("/calendar/v3/calendars/" + encodedCalendarId + "/events", url.encodedPath());
            assertEquals("10", url.queryParameter("maxResults"));
            assertEquals(TaskAggregatorApiConstants.ORDER_BY_START_TIME, url.queryParameter("orderBy"));
            assertEquals("true", url.queryParameter("singleEvents"));
            assertNotNull(url.queryParameter("timeMin"));

            util.verify(() -> TaskAggregatorApiUtil.formatEvents(anyList()));
            assertSame(formatted, output);
        }
    }

    @Test
    void getUpcomingEvents_fails() throws Exception {
        mockWebServer.shutdown();

        assertThrows(
            Exception.class,
            () -> googleEventsServiceImpl.getUpcomingCalendarEvents()
        );
    }

    @Test
    void getTodaysEvents() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {
                    "items": [
                        { "id": "Item1", "title": "Today Event" }
                    ]
                }
            """));
        
        CalendarEvent formattedEvent = new CalendarEvent();
        formattedEvent.setId("FormattedItem1");
        formattedEvent.setTitle("Formatted Today");

        List<CalendarEvent> formatted = List.of(formattedEvent);

        try (MockedStatic<TaskAggregatorApiUtil> util = mockStatic(TaskAggregatorApiUtil.class)) {
            util.when(() -> TaskAggregatorApiUtil.formatEvents(anyList())).thenReturn(formatted);

            List<CalendarEvent> output = googleEventsServiceImpl.getTodaysEvents();
            RecordedRequest request = mockWebServer.takeRequest();
            HttpUrl url = request.getRequestUrl();
            String encodedCalendarId = URLEncoder.encode(
                TaskAggregatorApiConstants.PRIMARY_CALENDAR_NAME,
                StandardCharsets.UTF_8
            );

            assertNotNull(output);
            assertEquals("GET", request.getMethod());
            assertNotNull(url);
            assertEquals("/calendar/v3/calendars/" + encodedCalendarId + "/events", url.encodedPath());
            assertEquals("true", url.queryParameter("singleEvents"));
            assertEquals(TaskAggregatorApiConstants.ORDER_BY_START_TIME, url.queryParameter("orderBy"));
            assertNotNull(url.queryParameter("timeMin"));
            assertNotNull(url.queryParameter("timeMax"));

            util.verify(() -> TaskAggregatorApiUtil.formatEvents(anyList()));
            assertSame(formatted, output);
        }
    }

    @Test
    void getUpcomingCalendarEvents_empty() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(""));

        assertEquals(googleEventsServiceImpl.getUpcomingCalendarEvents(), new ArrayList<>());
    }

    @Test
    void getTodaysEvents_fails() throws Exception {
        mockWebServer.shutdown();

        assertThrows(
            Exception.class,
            () -> googleEventsServiceImpl.getTodaysEvents()
        );
    }
}
