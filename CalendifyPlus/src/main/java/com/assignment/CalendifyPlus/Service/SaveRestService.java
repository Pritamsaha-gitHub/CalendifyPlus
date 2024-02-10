package com.assignment.CalendifyPlus.Service;

import com.assignment.CalendifyPlus.Controller.RetriveRestController;
import com.assignment.CalendifyPlus.Enum.EventType;
import com.assignment.CalendifyPlus.Model.ApplicationUser;
import com.assignment.CalendifyPlus.Model.Event;
import com.assignment.CalendifyPlus.Repository.RepositoryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class SaveRestService {

    @Autowired
    RepositoryHandler repositoryHandler;
    @Autowired
    RetriveRestController retriveRestController;

    public ResponseEntity<String> createUser(ApplicationUser user){
        repositoryHandler.save(user);
        return new ResponseEntity<>("User Register Sucessfully", HttpStatus.OK);
    }

    public ResponseEntity<String> createEvent(Event event) {
        try {
            repositoryHandler.save(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the event: " + e.getMessage());
        }

        String eventId = event.get_id();
        LocalDateTime startDateTime = event.getStartTime();

        if (event.getEventType().equals(EventType.PERSONALEVENT)) {
            handlePersonalEventCreation(event, eventId, startDateTime);
        } else {
            // Handle other types of events if needed
        }
        return ResponseEntity.ok("Event created successfully.");
    }

    private void handlePersonalEventCreation(Event event, String eventId, LocalDateTime startDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = event.getEndTime().toLocalTime();

        ApplicationUser user = retriveRestController.getUser(event.getCreatedById()).getBody();
        Map<String, List<List<String>>> eventMap = user.getEventLists();

        if (eventMap == null) {
            eventMap = new HashMap<>();
            user.setEventLists(eventMap);
        }

        List<List<String>> timeMap = eventMap.computeIfAbsent(startDate.toString(), k -> new ArrayList<>());
        timeMap.add(Arrays.asList(startTime.toString(), endTime.toString(), eventId));

        repositoryHandler.save(user);
    }


}
