package com.eclark.task_aggregator_api.model.googleTasks;

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
public class TaskResponse {
    public String message;
    public String title;
}
