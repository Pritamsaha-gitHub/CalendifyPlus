//package com.assignment.CalendifyPlus.Service;
//
//import com.assignment.CalendifyPlus.Dto.EventRequestDto;
//import com.assignment.CalendifyPlus.Dto.SlotRequestDto;
//import com.assignment.CalendifyPlus.Model.ApplicationUser;
//import com.assignment.CalendifyPlus.Model.Event;
//import com.assignment.CalendifyPlus.Repository.RepositoryHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Formatter;
//import java.util.List;
//
//@Service
//public class RetriveRestService {
//
//    @Autowired
//    RepositoryHandler repositoryHandler;
//
//    public ResponseEntity<ApplicationUser> getUser(String id) {
//        List<ApplicationUser> applicationUser = repositoryHandler.findByField("_id",id,ApplicationUser.class);
//        if (applicationUser.size() > 0 && applicationUser.get(0) != null) {
//            return new ResponseEntity<>(applicationUser.get(0), HttpStatus.FOUND);
//        }
//        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
//    }
//
//    public ResponseEntity<List<Event>> fetchEvent(EventRequestDto eventRequestDto) {
//        List<ApplicationUser> user = repositoryHandler.findByField("_id",eventRequestDto.getUserId(), ApplicationUser.class);
//        List<Event> events = new ArrayList<>();
//        List<List<String>> eventList = user.get(0).getEventLists().get(eventRequestDto.getDate());
//        for (List currentList : eventList) {
//            String eventId =currentList.get(2).toString();
//            events.add(repositoryHandler.findByField("_id",eventId, Event.class).get(0));
//        }
//        return new ResponseEntity<>(events,HttpStatus.OK);
//    }
//
//    public ResponseEntity fetchConflict(@RequestBody EventRequestDto eventRequestDto) {
//        List<ApplicationUser> user = repositoryHandler.findByField("_id",eventRequestDto.getUserId(), ApplicationUser.class);
//        List<List<String>> eventList = user.get(0).getEventLists().get(eventRequestDto.getDate());
//        List<List<LocalTime>> lacaltimeList = new ArrayList<>();
//
//        for (List currentList : eventList) {
//            String eventId =currentList.get(2).toString();
//            lacaltimeList.add(Arrays.asList(localTimeConverter(currentList.get(0).toString()),localTimeConverter(currentList.get(1).toString())));
//        }
//        lacaltimeList.sort((list1, list2) -> list1.get(0).compareTo(list2.get(0)));
//
//        List<String> conflictResult = new ArrayList<>();
//        for (int i = 1; i < lacaltimeList.size(); i++) {
//            LocalTime previousEndtime = lacaltimeList.get(i-1).get(1);
//            LocalTime currentStart = lacaltimeList.get(i).get(0);
//
//
//            if (!previousEndtime.isBefore(currentStart)) {
//                String previousConflict = eventRequestDto.getDate() + " -> " + lacaltimeList.get(i-1).get(0) + " " + eventRequestDto.getDate() + " -> " + previousEndtime;
//                String currentConflict = eventRequestDto.getDate() + " -> " + currentStart + " " + eventRequestDto.getDate() + " -> " + lacaltimeList.get(i).get(1);
//                if (!conflictResult.contains(previousConflict)) conflictResult.add(previousConflict);
//                if (!conflictResult.contains(currentConflict)) conflictResult.add(currentConflict);
//            }
//        }
//        if (conflictResult.size() == 0) return new ResponseEntity<>("There is no conflict in" + eventRequestDto.getDate(),HttpStatus.OK);
//        return new ResponseEntity<>(conflictResult,HttpStatus.OK);
//    }
//
//    public LocalTime localTimeConverter(String time) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return LocalTime.parse(time,formatter);
//    }
//
//    public ResponseEntity<String> fetchUpcomingSlot(SlotRequestDto slotRequestDto) {
//
//        List<List<LocalTime>> lacaltimeList = new ArrayList<>();
//        for (String userId : slotRequestDto.getRequiredUserIds()) {
//            List<ApplicationUser> user = repositoryHandler.findByField("_id",userId, ApplicationUser.class);
//            List<List<String>> eventList = user.get(0).getEventLists().get(slotRequestDto.getDate());
//            for (List currentList : eventList) {
//                String eventId =currentList.get(2).toString();
//                lacaltimeList.add(Arrays.asList(localTimeConverter(currentList.get(0).toString()),localTimeConverter(currentList.get(1).toString())));
//            }
//        }
//        lacaltimeList.sort((list1, list2) -> list1.get(0).compareTo(list2.get(0)));
//
//        List<List<LocalTime>> finalEventTimeList = new ArrayList<>();
//        LocalTime currentStartTime = null;
//        LocalTime currentEndTime = null;
//
//        for (List<LocalTime> timeRange : lacaltimeList) {
//            LocalTime startTime = timeRange.get(0);
//            LocalTime endTime = timeRange.get(1);
//            if (currentStartTime == null && currentEndTime == null) {
//                // Initialize current start and end times
//                currentStartTime = startTime;
//                currentEndTime = endTime;
//            } else {
//                if (startTime.isBefore(currentEndTime) || startTime.equals(currentEndTime)) {
//                    // If the current event overlaps with the previous one or starts immediately after it
//                    currentEndTime = currentEndTime.isBefore(endTime) ? endTime : currentEndTime;
//                } else {
//                    // If there's a gap between the current and previous event
//                    finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
//                    currentStartTime = startTime;
//                    currentEndTime = endTime;
//                }
//            }
//        }
//
//        // Add the last event time range
//        if (currentStartTime != null && currentEndTime != null) {
//            finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
//        }
////        for (int i = 0; i < finalEventTimeList.size(); i++) {
////            System.out.println(i + "th element in list " + finalEventTimeList.get(i));
////        }
//
//        int durationInHours = slotRequestDto.getDuration(); // Duration of the event in hours
//        List<List<LocalTime>> availableSlots = new ArrayList<>();
//
//// Total duration of the day (assuming from 00:00 to 23:59)
//        LocalTime startOfDay = LocalTime.of(0, 0);
//        LocalTime endOfDay = LocalTime.of(23, 59);
//
//// Iterate over the day in 2-hour slots
//        for (LocalTime startTime = startOfDay; startTime.isBefore(endOfDay); startTime = startTime.plusHours(durationInHours)) {
//            LocalTime endTime = startTime.plusHours(durationInHours);
//
//            // Check if this 2-hour slot overlaps with any busy time for any user
//            boolean isAvailable = true;
//            for (List<LocalTime> busyTimeRange : finalEventTimeList) {
//                LocalTime busyStartTime = busyTimeRange.get(0);
//                LocalTime busyEndTime = busyTimeRange.get(1);
//
//                if (!(endTime.isBefore(busyStartTime) || startTime.isAfter(busyEndTime))) {
//                    // There's an overlap, mark this slot as unavailable
//                    isAvailable = false;
//                    break;
//                }
//            }
//
//            // If the slot is available, add it to the list of available slots
//            if (isAvailable) {
//                availableSlots.add(Arrays.asList(startTime, endTime));
//            }
//        }
//
//// Print the available slots
//        for (int i = 0; i < availableSlots.size(); i++) {
//            System.out.println(i + "th available slot: " + availableSlots.get(i).get(0) + " - " + availableSlots.get(i).get(1));
//        }
//
//        return new ResponseEntity<>("ok",HttpStatus.OK);
//    }
//
//}
























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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

@Service
public class RetriveRestService {

    @Autowired
    RepositoryHandler repositoryHandler;

    public ResponseEntity<ApplicationUser> getUser(String id) {
        List<ApplicationUser> applicationUser = repositoryHandler.findByField("_id",id,ApplicationUser.class);
        if (applicationUser.size() > 0 && applicationUser.get(0) != null) {
            return new ResponseEntity<>(applicationUser.get(0), HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Event>> fetchEvent(EventRequestDto eventRequestDto) {
        List<ApplicationUser> user = repositoryHandler.findByField("_id",eventRequestDto.getUserId(), ApplicationUser.class);
        List<Event> events = new ArrayList<>();
        List<List<String>> eventList = user.get(0).getEventLists().get(eventRequestDto.getDate());
        for (List currentList : eventList) {
            String eventId =currentList.get(2).toString();
            events.add(repositoryHandler.findByField("_id",eventId, Event.class).get(0));
        }
        return new ResponseEntity<>(events,HttpStatus.OK);
    }

    public ResponseEntity fetchConflict(@RequestBody EventRequestDto eventRequestDto) {
        List<ApplicationUser> user = repositoryHandler.findByField("_id",eventRequestDto.getUserId(), ApplicationUser.class);
        List<List<String>> eventList = user.get(0).getEventLists().get(eventRequestDto.getDate());
        List<List<LocalTime>> lacaltimeList = new ArrayList<>();

        for (List currentList : eventList) {
            String eventId =currentList.get(2).toString();
            lacaltimeList.add(Arrays.asList(localTimeConverter(currentList.get(0).toString()),localTimeConverter(currentList.get(1).toString())));
        }
        lacaltimeList.sort((list1, list2) -> list1.get(0).compareTo(list2.get(0)));

        List<String> conflictResult = new ArrayList<>();
        for (int i = 1; i < lacaltimeList.size(); i++) {
            LocalTime previousEndtime = lacaltimeList.get(i-1).get(1);
            LocalTime currentStart = lacaltimeList.get(i).get(0);


            if (!previousEndtime.isBefore(currentStart)) {
                String previousConflict = eventRequestDto.getDate() + " -> " + lacaltimeList.get(i-1).get(0) + " " + eventRequestDto.getDate() + " -> " + previousEndtime;
                String currentConflict = eventRequestDto.getDate() + " -> " + currentStart + " " + eventRequestDto.getDate() + " -> " + lacaltimeList.get(i).get(1);
                if (!conflictResult.contains(previousConflict)) conflictResult.add(previousConflict);
                if (!conflictResult.contains(currentConflict)) conflictResult.add(currentConflict);
            }
        }
        if (conflictResult.size() == 0) return new ResponseEntity<>("There is no conflict in" + eventRequestDto.getDate(),HttpStatus.OK);
        return new ResponseEntity<>(conflictResult,HttpStatus.OK);
    }

    public LocalTime localTimeConverter(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time,formatter);
    }

    public ResponseEntity<String> fetchUpcomingSlot(SlotRequestDto slotRequestDto) {

//        List<List<LocalTime>> lacaltimeList = new ArrayList<>();
//        for (String userId : slotRequestDto.getRequiredUserIds()) {
//            List<ApplicationUser> user = repositoryHandler.findByField("_id",userId, ApplicationUser.class);
//            List<List<String>> eventList = user.get(0).getEventLists().get(slotRequestDto.getDate());
//            for (List currentList : eventList) {
//                String eventId =currentList.get(2).toString();
//                lacaltimeList.add(Arrays.asList(localTimeConverter(currentList.get(0).toString()),localTimeConverter(currentList.get(1).toString())));
//            }
//        }
//        lacaltimeList.sort((list1, list2) -> list1.get(0).compareTo(list2.get(0)));
//
//        List<List<LocalTime>> finalEventTimeList = new ArrayList<>();
//        LocalTime currentStartTime = null;
//        LocalTime currentEndTime = null;
//
//        for (List<LocalTime> timeRange : lacaltimeList) {
//            LocalTime startTime = timeRange.get(0);
//            LocalTime endTime = timeRange.get(1);
//            if (currentStartTime == null && currentEndTime == null) {
//                // Initialize current start and end times
//                currentStartTime = startTime;
//                currentEndTime = endTime;
//            } else {
//                if (startTime.isBefore(currentEndTime) || startTime.equals(currentEndTime)) {
//                    // If the current event overlaps with the previous one or starts immediately after it
//                    currentEndTime = currentEndTime.isBefore(endTime) ? endTime : currentEndTime;
//                } else {
//                    // If there's a gap between the current and previous event
//                    finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
//                    currentStartTime = startTime;
//                    currentEndTime = endTime;
//                }
//            }
//        }
//
//        // Add the last event time range
//        if (currentStartTime != null && currentEndTime != null) {
//            finalEventTimeList.add(Arrays.asList(currentStartTime, currentEndTime));
//        }
////        for (int i = 0; i < finalEventTimeList.size(); i++) {
////            System.out.println(i + "th element in list " + finalEventTimeList.get(i));
////        }
//
//        int durationInHours = slotRequestDto.getDuration(); // Duration of the event in hours
//        List<List<LocalTime>> availableSlots = new ArrayList<>();
//
//// Total duration of the day (assuming from 00:00 to 23:59)
//        LocalTime startOfDay = LocalTime.of(0, 0);
//        LocalTime endOfDay = LocalTime.of(23, 59);
//
//// Iterate over the day in 2-hour slots
//        for (LocalTime startTime = startOfDay; startTime.isBefore(endOfDay); startTime = startTime.plusHours(durationInHours)) {
//            LocalTime endTime = startTime.plusHours(durationInHours);
//
//            // Check if this 2-hour slot overlaps with any busy time for any user
//            boolean isAvailable = true;
//            for (List<LocalTime> busyTimeRange : finalEventTimeList) {
//                LocalTime busyStartTime = busyTimeRange.get(0);
//                LocalTime busyEndTime = busyTimeRange.get(1);
//
//                if (!(endTime.isBefore(busyStartTime) || startTime.isAfter(busyEndTime))) {
//                    // There's an overlap, mark this slot as unavailable
//                    isAvailable = false;
//                    break;
//                }
//            }
//
//            // If the slot is available, add it to the list of available slots
//            if (isAvailable) {
//                availableSlots.add(Arrays.asList(startTime, endTime));
//            }
//        }
//
//// Print the available slots
//        for (int i = 0; i < availableSlots.size(); i++) {
//            System.out.println(i + "th available slot: " + availableSlots.get(i).get(0) + " - " + availableSlots.get(i).get(1));
//        }
//
//        return new ResponseEntity<>("ok",HttpStatus.OK);
    }

}