package com.assignment.CalendifyPlus.Controller;

import com.assignment.CalendifyPlus.Dto.EventRequestDto;
import com.assignment.CalendifyPlus.Dto.SlotRequestDto;
import com.assignment.CalendifyPlus.Model.ApplicationUser;
import com.assignment.CalendifyPlus.Model.Event;
import com.assignment.CalendifyPlus.Service.RetriveRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/rest/retrive")
public class RetriveRestController {

    @Autowired
    RetriveRestService retriveRestService;

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApplicationUser> getUser(@PathVariable String id) {
        return retriveRestService.getUser(id);
    }

    @GetMapping("/fetchEvent")
    public ResponseEntity<List<Event>> fetchEvent(@RequestBody EventRequestDto eventRequestDto) {
        return retriveRestService.fetchEvent(eventRequestDto);
    }

    @GetMapping("/fetchConflict")
    public ResponseEntity<String> fetchConflict(@RequestBody EventRequestDto eventRequestDto) {
        return retriveRestService.fetchConflict(eventRequestDto);
    }

    @GetMapping("/fetchUpcomingSlot")
    public ResponseEntity fetchUpcomingSlot(@RequestBody SlotRequestDto slotRequestDto) {
        return retriveRestService.fetchUpcomingSlot(slotRequestDto);
    }
}
