package com.assignment.CalendifyPlus.Service;

import com.assignment.CalendifyPlus.Dto.EventRequestDto;
import com.assignment.CalendifyPlus.Dto.SlotRequestDto;
import com.assignment.CalendifyPlus.Model.ApplicationUser;
import com.assignment.CalendifyPlus.Model.Event;
import com.assignment.CalendifyPlus.Repository.RepositoryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RetriveRestService {

    @Autowired
    RepositoryHandler repositoryHandler;

    public ResponseEntity<ApplicationUser> getUser(String id) {
        try {
            List<ApplicationUser> applicationUser = repositoryHandler.findByField("_id", id, ApplicationUser.class);
            if (!applicationUser.isEmpty() && applicationUser.get(0) != null) {
                return new ResponseEntity<>(applicationUser.get(0), HttpStatus.FOUND);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches events for a given user and date.
     *
     * @param eventRequestDto The data transfer object containing user ID and date.
     * @return ResponseEntity containing a list of events for the user on the specified date,
     *         or an error message if there are no events or if an invalid event ID is encountered.
     */
    public ResponseEntity fetchEvent(EventRequestDto eventRequestDto) {
        List<ApplicationUser> user = repositoryHandler.findByField("_id",eventRequestDto.getUserId(), ApplicationUser.class);
        List<Event> events = new ArrayList<>();
        if(user.get(0).getEventLists().get(eventRequestDto.getDate()) != null){
            List<List<String>> eventList = user.get(0).getEventLists().get(eventRequestDto.getDate());
            if (eventList != null && eventList.size() > 0) {
                for (List currentList : eventList) {
                    String eventId = currentList.get(2).toString();
                    try{
                        events.add(repositoryHandler.findByField("_id", eventId, Event.class).get(0));
                    }catch (Exception e) {
                        return new ResponseEntity<>("EventId : " + eventId + " is a invalid Data", HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>(events, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(events,HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(events,HttpStatus.OK);
        }

    }


    /**
     * Fetches conflicts for events of a specific user on a given date.
     *
     * @param eventRequestDto The data transfer object containing user ID and date.
     * @return ResponseEntity containing a list of conflicts if any conflicts are found,
     *         a message indicating no conflicts if no conflicts are found,
     *         or an error message if there's an issue during conflict resolution.
     */
    public ResponseEntity fetchConflict(@RequestBody EventRequestDto eventRequestDto) {
        try {
            List<ApplicationUser> users = repositoryHandler.findByField("_id", eventRequestDto.getUserId(), ApplicationUser.class);
            if (users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<List<String>> eventList = users.get(0).getEventLists().get(eventRequestDto.getDate());
            if (eventList == null || eventList.isEmpty()) {
                return ResponseEntity.ok("No events found for the specified date.");
            }

            List<List<LocalTime>> localTimeList = new ArrayList<>();

            for (List<String> currentList : eventList) {
                LocalTime startTime = localTimeConverter(currentList.get(0));
                LocalTime endTime = localTimeConverter(currentList.get(1));
                localTimeList.add(Arrays.asList(startTime, endTime));
            }
            localTimeList.sort((list1, list2) -> list1.get(0).compareTo(list2.get(0)));

            List<String> conflictResult = new ArrayList<>();
            for (int i = 1; i < localTimeList.size(); i++) {
                LocalTime previousEndTime = localTimeList.get(i - 1).get(1);
                LocalTime currentStartTime = localTimeList.get(i).get(0);

                if (!previousEndTime.isBefore(currentStartTime)) {
                    String previousConflict = String.format("Conflict: Event from %s to %s overlaps with another event on %s from %s to %s",
                            eventRequestDto.getDate(), localTimeList.get(i - 1).get(0),
                            eventRequestDto.getDate(), localTimeList.get(i - 1).get(0),
                            previousEndTime);

                    String currentConflict = String.format("Conflict: Event from %s to %s overlaps with another event on %s from %s to %s",
                            eventRequestDto.getDate(), currentStartTime,
                            eventRequestDto.getDate(), currentStartTime,
                            localTimeList.get(i).get(1));
                    if (!conflictResult.contains(previousConflict)) conflictResult.add(previousConflict);
                    if (!conflictResult.contains(currentConflict)) conflictResult.add(currentConflict);
                }
            }

            if (conflictResult.isEmpty()) {
                return ResponseEntity.ok("There is no conflict in " + eventRequestDto.getDate());
            } else {
                return ResponseEntity.ok(conflictResult);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching conflicts: " + e.getMessage());
        }
    }

    public LocalTime localTimeConverter(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time,formatter);
    }

    /**
     * Fetches upcoming slots of free time for the required users on a specific date,
     * considering their existing events and a required duration for the slot.
     *
     * @param slotRequestDto The data transfer object containing required user IDs, date, and slot duration.
     * @return ResponseEntity containing a list of upcoming slots of free time satisfying the duration,
     *         or an error message if there's an issue during slot calculation.
     */

    public ResponseEntity<List<String>> fetchUpcomingSlot(SlotRequestDto slotRequestDto) {
        List<List<LocalTime>> eventTimeList = fetchEventTimeList(slotRequestDto);
        List<List<LocalTime>> freeTimeIntervals = calculateFreeTimeIntervals(eventTimeList);
        List<String> optimalTimeIntervals = findOptimalTimeIntervals(freeTimeIntervals, slotRequestDto.getDuration());

        return new ResponseEntity<>(optimalTimeIntervals, HttpStatus.OK);
    }

    /**
     * Fetches event time intervals for the required users on a specific date.
     *
     * @param slotRequestDto The data transfer object containing required user IDs and date.
     * @return List of event time intervals for the required users on the specified date.
     */
    private List<List<LocalTime>> fetchEventTimeList(SlotRequestDto slotRequestDto) {
        List<List<LocalTime>> eventTimeList = new ArrayList<>();
        for (String userId : slotRequestDto.getRequiredUserIds()) {
            List<ApplicationUser> users = repositoryHandler.findByField("_id", userId, ApplicationUser.class);
            if (!users.isEmpty()) {
                ApplicationUser user = users.get(0);
                Map<String, List<List<String>>> eventLists = user.getEventLists();
                if (eventLists != null) {
                    List<List<String>> eventList = eventLists.get(slotRequestDto.getDate());
                    if (eventList != null && !eventList.isEmpty()) {
                        for (List<String> currentList : eventList) {
                            LocalTime startTime = localTimeConverter(currentList.get(0));
                            LocalTime endTime = localTimeConverter(currentList.get(1));
                            eventTimeList.add(Arrays.asList(startTime, endTime));
                        }
                    }
                }
            }
        }
        return eventTimeList;
    }

    /**
     * Calculates free time intervals based on event time intervals.
     *
     * @param eventTimeList List of event time intervals.
     * @return List of free time intervals.
     */
    private List<List<LocalTime>> calculateFreeTimeIntervals(List<List<LocalTime>> eventTimeList) {
        List<List<LocalTime>> finalEventTimeList = new ArrayList<>();
        LocalTime currentStartTime = null;
        LocalTime currentEndTime = null;

        for (List<LocalTime> timeRange : eventTimeList) {
            LocalTime startTime = timeRange.get(0);
            LocalTime endTime = timeRange.get(1);
            if (currentStartTime == null && currentEndTime == null) {
                // Initialize current start and end times
                currentStartTime = startTime;
                currentEndTime = endTime;
            } else {
                if (startTime.isBefore(currentEndTime) || startTime.equals(currentEndTime)) {
                    // If the current event overlaps with the previous one or starts immediately after it
                    currentEndTime = currentEndTime.isBefore(endTime) ? endTime : currentEndTime;
                } else {
                    // If there's a gap between the current and previous event
                    finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
                    currentStartTime = startTime;
                    currentEndTime = endTime;
                }
            }
        }

        // Add the last event time range
        if (currentStartTime != null && currentEndTime != null) {
            finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
        }

        List<List<LocalTime>> freeTimeIntervals = new ArrayList<>();

        if (finalEventTimeList.isEmpty()) {
            // If no events, the entire day is free
            freeTimeIntervals.add(Arrays.asList(LocalTime.of(0, 0), LocalTime.of(23, 59, 59)));
        } else {
            // Iterate over the occupied time intervals to find free time intervals
            LocalTime lastEndTime = LocalTime.of(0, 0);
            for (List<LocalTime> event : finalEventTimeList) {
                LocalTime eventStartTime = event.get(0);
                LocalTime eventEndTime = event.get(1);

                // Check if there's a gap between the last event and the current event
                if (!eventStartTime.equals(lastEndTime)) {
                    freeTimeIntervals.add(Arrays.asList(lastEndTime, eventStartTime));
                }

                lastEndTime = eventEndTime;
            }

            // Check if there's a gap after the last event until the end of the day
            if (!lastEndTime.equals(LocalTime.of(23, 59, 59))) {
                freeTimeIntervals.add(Arrays.asList(lastEndTime, LocalTime.of(23, 59, 59)));
            }
        }

        return freeTimeIntervals;
    }

    /**
     * Finds optimal time intervals satisfying the required duration.
     *
     * @param freeTimeIntervals List of free time intervals.
     * @param duration The required duration for the slot.
     * @return List of optimal time intervals satisfying the required duration.
     */

    private List<String> findOptimalTimeIntervals(List<List<LocalTime>> freeTimeIntervals, int duration) {
        List<String> optimalTimeIntervals = new ArrayList<>();
        for (List<LocalTime> interval : freeTimeIntervals) {
            long durationInHours = interval.get(0).until(interval.get(1), java.time.temporal.ChronoUnit.HOURS);
            if (durationInHours >= duration) {
                String optimalInterval = "Most optimal time is between starttime: " + interval.get(0) +
                        " to endtime: " + interval.get(1);
                optimalTimeIntervals.add(optimalInterval);
            }
        }
        return optimalTimeIntervals;
    }

}