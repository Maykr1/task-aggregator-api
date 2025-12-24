package com.eclark.task_aggregator_api.model.googleTasks;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString @Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private String title;
    private String updated;
    private String selfLink;
    private String parent;
    private String notes;
    private String status;
    private String due;
    private List<String> links;
    private String webViewLink;
    private List<Task> children;
}
