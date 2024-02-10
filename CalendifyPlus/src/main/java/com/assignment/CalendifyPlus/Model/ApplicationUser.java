package com.assignment.CalendifyPlus.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "application_users")
public class ApplicationUser extends BaseEntity{

    private String userName;
    private String email;
    Map<String, List<List<String>>> eventLists;
//    Map<String,Map<String, List<List<String>>>> eventLists;
//    Map<String,String> groupeventLists;
}
