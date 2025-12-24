package com.eclark.task_aggregator_api.model.googleTasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    public String title;
    public String notes;
    public String due;
}
