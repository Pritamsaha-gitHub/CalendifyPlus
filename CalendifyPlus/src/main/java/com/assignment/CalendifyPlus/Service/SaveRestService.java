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


    /**
     * Creates an event and saves it in the repository if the event's start time is valid.
     * If the start time is in the past, returns a message indicating that the event cannot be created.
     * Handles personal events differently based on the event type.
     *
     * @param event The event object to be created.
     * @return ResponseEntity containing a success message if the event is created successfully,
     *         or an error message if there's an issue saving the event.
     */

    public ResponseEntity<String> createEvent(Event event) {
        if(isEventTimeValid(event.getStartTime(),event.getEndTime())) {
            try {
                repositoryHandler.save(event);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the event: " + e.getMessage());
            }

            String eventId = event.get_id();
            LocalDateTime startDateTime = event.getStartTime();

            if (event.getEventType().equals(EventType.PERSONALEVENT)) {
                handlePersonalEventCreation(event, eventId, startDateTime,event.getApplicationUsersId().get(0));
            } else {
                // Handle other types of events if needed
                List<String> applicationUsersId = event.getApplicationUsersId();
                for (String currentId : applicationUsersId) {
                    handlePersonalEventCreation(event, eventId, startDateTime,currentId);
                }
            }
            return ResponseEntity.ok("Event created successfully.");
        } else {
            return ResponseEntity.ok("You can't Create a Event in Past.");
        }

    }

    /**
     * Handles the creation of a personal event for a specific user.
     *
     * @param event The event object to be created.
     * @param eventId The unique identifier of the event.
     * @param startDateTime The start date and time of the event.
     * @param userId The unique identifier of the user for whom the event is being created.
     */

    private void handlePersonalEventCreation(Event event, String eventId, LocalDateTime startDateTime , String userId) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = event.getEndTime().toLocalTime();

        ApplicationUser user = retriveRestController.getUser(userId).getBody();
        Map<String, List<List<String>>> eventMap = user.getEventLists();

        if (eventMap == null) {
            eventMap = new HashMap<>();
            user.setEventLists(eventMap);
        }

        List<List<String>> timeMap = eventMap.computeIfAbsent(startDate.toString(), k -> new ArrayList<>());
        timeMap.add(Arrays.asList(startTime.toString(), endTime.toString(), eventId));

        repositoryHandler.save(user);
    }

    /**
     * Checks if the provided event start time and end time are valid.
     *
     * @param startTime The start date and time of the event.
     * @param endTime The end date and time of the event.
     * @return true if the start time is after the current time and the end time is after the start time;
     *         otherwise, false.
     */
    public boolean isEventTimeValid(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime currentTime = LocalDateTime.now();

        // Check if startTime is greater than current time
        boolean isStartTimeValid = startTime.isAfter(currentTime);

        // Check if endTime is greater than startTime
        boolean isEndTimeValid = endTime.isAfter(startTime);

        // Return true if both conditions are met
        return isStartTimeValid && isEndTimeValid;
    }
}
