package org.example.HotelBackend.cotrollers;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.models.Reservation;
import org.example.HotelBackend.models.Room;
import org.example.HotelBackend.services.PeopleService;
import org.example.HotelBackend.services.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomsController {

    private final RoomsService roomsService;
    private final PeopleService peopleService;

    @Autowired
    public RoomsController(RoomsService roomsService, PeopleService peopleService) {
        this.roomsService = roomsService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String showRooms(Model model) {

        model.addAttribute("rooms", roomsService.getRooms());
        model.addAttribute("roomOccupancy", roomsService.getRoomsOccupancy());
        model.addAttribute("role", peopleService.getCurrentUser().getRole());
        return "rooms/index";
    }

    @GetMapping("/{id}")
    public String showRoomById(@PathVariable("id") int id, Model model) {
        Room room = roomsService.getRoom(id);
        model.addAttribute("room", room);


        model.addAttribute("role", peopleService.getCurrentUser().getRole());

        if (roomsService.getCurrentReservation(id).isPresent()) {
            Optional<Reservation> reservation = roomsService.getCurrentReservation(id);
            model.addAttribute("reservation", roomsService.getCurrentReservation(id));
            model.addAttribute("owner", reservation.get().getOwner());
        }

        return "rooms/show";
    }

    @GetMapping("/new")
    public String newRoom(@ModelAttribute("room") Room room) {
        return "rooms/add";
    }

    @PostMapping()
    public String addRoom(@ModelAttribute("room") Room room, BindingResult result) {

        if (result.hasErrors()) {
            return "rooms/add";
        }

        roomsService.addRoom(room);
        return "redirect:/rooms/" + room.getId();
    }

    @GetMapping("/{id}/edit")
    public String editRoom(@PathVariable("id") int id, Model model) {
        model.addAttribute("room", roomsService.getRoom(id));
        return "rooms/edit";
    }

    @PatchMapping("/{id}")
    public String updateRoom(@PathVariable("id") int id, @ModelAttribute("room") Room room, BindingResult result) {
        if (result.hasErrors()) {
            return "rooms/edit";
        }

        roomsService.updateRoom(id, room);
        return "redirect:/rooms/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") int id) {
        roomsService.deleteRoom(id);
        return "redirect:/rooms";
    }

}
