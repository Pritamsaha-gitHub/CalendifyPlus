package com.assignment.CalendifyPlus.Controller;

import com.assignment.CalendifyPlus.Model.ApplicationUser;
import com.assignment.CalendifyPlus.Model.Event;
import com.assignment.CalendifyPlus.Service.SaveRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rest/save")
public class SaveRestController {

    @Autowired
    SaveRestService saveRestService;

    @PostMapping("/addNewUser")
    public ResponseEntity<String> createUser(@RequestBody ApplicationUser user) {
        return saveRestService.createUser(user);
    }
    @PostMapping("/addEvent")
    public ResponseEntity<String> createEvent (@RequestBody Event event) {
        return saveRestService.createEvent(event);
    }
}
