package com.eclark.task_aggregator_api.model.googleCalendar;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
    private String id;
    private String status;
    private String htmlLink;
    private String created;
    private String updated;

    @JsonProperty("summary")
    private String title;
    
    private Start start;
    private End end;
}
