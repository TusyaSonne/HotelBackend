package org.example.HotelBackend.cotrollers;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.services.PeopleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final PeopleService peopleService;

    public IndexController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/")
    public String index(Model model) {

        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("person", currentUser);

        return "home/index";
    }
}
