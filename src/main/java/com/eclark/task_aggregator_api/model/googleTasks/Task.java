package com.eclark.task_aggregator_api.model.googleTasks;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private String kind;
    private String etag;
    private String title;
    private String updated;
    private String selfLink;
    private String parent;
    private String position;
    private String notes;
    private String status;
    private String due;
    private List<String> links;
    private String webViewLink;
    private List<Task> children;
}
