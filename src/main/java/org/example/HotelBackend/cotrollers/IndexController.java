package org.example.HotelBackend.cotrollers;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.services.PeopleService;
import org.example.HotelBackend.services.RoomsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private final PeopleService peopleService;
    private final RoomsService roomsService;

    public IndexController(PeopleService peopleService, RoomsService roomsService) {
        this.peopleService = peopleService;
        this.roomsService = roomsService;
    }

    @GetMapping("/")
    public String index(Model model) {

        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("person", currentUser);

        // Получение информации о занятых номерах для администратора
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            Map<Integer, Boolean> roomOccupancy = roomsService.getRoomsOccupancy();
            // Добавляем занятые номера в модель
            model.addAttribute("occupiedRooms", roomOccupancy.entrySet().stream()
                    .filter(Map.Entry::getValue)  // Оставляем только занятые номера
                    .map(Map.Entry::getKey)      // Извлекаем id номеров
                    .collect(Collectors.toList()));
        }

        return "home/index";
    }
}
