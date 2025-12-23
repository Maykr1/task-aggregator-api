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
public class Start {
    private String date;
    private String dateTime;
    private String timeZone;
}
