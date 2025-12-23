package com.eclark.task_aggregator_api.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.client.RestClient;

import com.eclark.task_aggregator_api.model.googleTasks.Task;
import com.eclark.task_aggregator_api.model.googleTasks.TaskList;
import com.eclark.task_aggregator_api.service.impl.GoogleTasksServiceImpl;
import com.eclark.task_aggregator_api.util.TaskAggregatorApiUtil;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class GoogleTasksServiceTests {
    private MockWebServer mockWebServer;
    private GoogleTasksServiceImpl googleTasksServiceImpl;

    @BeforeEach
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        RestClient restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();

        googleTasksServiceImpl = new GoogleTasksServiceImpl(restClient);
    }

    @AfterEach
    public void teardown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getAllLists() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                    {
                        "items": [
                            { "id": "A", "title": "Inbox" },
                            { "id": "B", "title": "School" }
                        ]
                    }
                    """));

        List<TaskList> lists = googleTasksServiceImpl.getAllLists();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/tasks/v1/users/@me/lists", request.getPath());
        assertNotNull(lists);
        assertEquals(2, lists.size());
        assertEquals("A", lists.get(0).getId());
        assertEquals("B", lists.get(1).getId());
    }

    @Test
    void getTasksById() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {
                    "items": [
                    { "id": "p1" },
                    { "id": "c1", "parent": "p1" }
                    ]
                }
            """));

        Task task1 = new Task();
        task1.setId("p1");

        List<Task> formatted = List.of(task1);

        try (MockedStatic<TaskAggregatorApiUtil> util = mockStatic(TaskAggregatorApiUtil.class)) {
            util.when(() -> TaskAggregatorApiUtil.formatTasks(anyList()))
                .thenReturn(formatted);

            List<Task> out = googleTasksServiceImpl.getTasksByListId("LIST_123");

            var req = mockWebServer.takeRequest();
            assertEquals("GET", req.getMethod());
            assertEquals("/tasks/v1/lists/LIST_123/tasks", req.getPath());

            util.verify(() -> TaskAggregatorApiUtil.formatTasks(anyList()), times(1));
            assertSame(formatted, out);
        }
    }

    @Test
    void getAllLists_bodyEmpty() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody(""));

        assertEquals(googleTasksServiceImpl.getAllLists(), new ArrayList<>());
    }

    @Test
    void getTasksByListId_bodyEmpty() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody(""));

        assertEquals(googleTasksServiceImpl.getTasksByListId("A"), new ArrayList<>());
    }

    @Test
    void getAllLists_fails() throws Exception {
        mockWebServer.shutdown();

        assertThrows(
            Exception.class,
            () -> googleTasksServiceImpl.getAllLists()
        );
    }

    @Test
    void getTaskListsById_fails() throws Exception {
        mockWebServer.shutdown();

        assertThrows(
            Exception.class,
            () -> googleTasksServiceImpl.getTasksByListId("A")
        );
    }
}
