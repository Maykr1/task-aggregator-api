package com.eclark.task_aggregator_api.model.googleCalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventRequest {
    public String summary;
    public String description;
    public Start start;
    public End end;
}
