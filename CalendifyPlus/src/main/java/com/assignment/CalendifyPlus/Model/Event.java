package com.assignment.CalendifyPlus.Model;

import com.assignment.CalendifyPlus.Enum.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "events")
public class Event extends BaseEntity{

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createdById;
    private String subject;
    private EventType eventType;
    private List<String> applicationUsersId;
}
