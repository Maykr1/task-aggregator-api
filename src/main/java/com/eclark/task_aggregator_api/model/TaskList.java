package com.eclark.task_aggregator_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskList {
    private String id;
    private String kind;
    private String etag;
    private String title;
    private String updated;
    private String selfLink;
}
