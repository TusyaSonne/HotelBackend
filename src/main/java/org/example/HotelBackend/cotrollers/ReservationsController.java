    package org.example.HotelBackend.cotrollers;

    import jakarta.validation.Valid;
    import org.example.HotelBackend.models.Person;
    import org.example.HotelBackend.models.Reservation;
    import org.example.HotelBackend.models.Room;
    import org.example.HotelBackend.services.PeopleService;
    import org.example.HotelBackend.services.ReservationsService;
    import org.example.HotelBackend.services.RoomsService;
    import org.example.HotelBackend.util.ReservationValidator;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RequestMapping("/reservations")
    @Controller
    public class ReservationsController {

        private final ReservationsService reservationsService;
        private final PeopleService peopleService;
        private final RoomsService roomsService;
        private final ReservationValidator reservationValidator;

        @Autowired
        public ReservationsController(ReservationsService reservationsService, PeopleService peopleService, RoomsService roomsService, ReservationValidator reservationValidator) {
            this.reservationsService = reservationsService;
            this.peopleService = peopleService;
            this.roomsService = roomsService;
            this.reservationValidator = reservationValidator;
        }


        @GetMapping()
        public String showReservations(Model model) {
            Person currentUser = peopleService.getCurrentUser();
            model.addAttribute("currentUser", currentUser);
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                model.addAttribute("reservations", reservationsService.getReservations());
            } else {
                //Для актуальности сессии (иначе будут старые данные)
                Person freshUser = peopleService.getPerson(currentUser.getId());
                List<Reservation> reservations = freshUser.getReservations();
                model.addAttribute("reservations", reservations);
            }

            return "reservations/index";
        }

        @GetMapping("/{id}")
        public String showReservationById(@PathVariable("id") int id, Model model) {
            Person currentUser = peopleService.getCurrentUser();

            Reservation reservation = reservationsService.getReservation(id);
            model.addAttribute("reservation", reservation);

            model.addAttribute("room", reservation.getRoom());

            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                model.addAttribute("owner", reservation.getOwner());
            }

            return "reservations/show";
        }

        @GetMapping("/new")
        public String newReservation(@ModelAttribute("reservation") Reservation reservation, Model model, @RequestParam(value = "roomId", required = false) Integer roomId) {

            Person currentUser = peopleService.getCurrentUser();

            if (roomId != null) {
                Room room = roomsService.getRoom(roomId);
                reservation.setRoom(room);
            }

            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                model.addAttribute("clients", peopleService.getPeople());
                model.addAttribute("rooms", roomsService.getRooms());
            } else {
                model.addAttribute("rooms", roomsService.getRooms());
            }

            return "reservations/add";
        }

        @PostMapping()
        public String addReservation(@ModelAttribute("reservation") @Valid Reservation reservation, BindingResult result, Model model) {

            //Валидация даты
            reservationValidator.validate(reservation, result);
            Person currentUser = peopleService.getCurrentUser();

            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                if (result.hasErrors()) {
                    // Добавление данных для повторного отображения списка клиентов и комнат
                    model.addAttribute("clients", peopleService.getPeople());
                    model.addAttribute("rooms", roomsService.getRooms());
                    return "reservations/add";
                }
            } else {
                reservation.setOwner(peopleService.getCurrentUser());
                if (result.hasErrors()) {
                    model.addAttribute("rooms", roomsService.getRooms());
                    return "reservations/add";
                }

            }



            reservationsService.addReservation(reservation);
            return "redirect:/reservations/" + reservation.getId();
        }

        @GetMapping("/{id}/edit")
        public String editReservation(@PathVariable("id") int id, Model model) {



            model.addAttribute("reservation", reservationsService.getReservation(id));
            model.addAttribute("rooms", roomsService.getRooms());

            return "reservations/edit";
        }

        @PatchMapping("/{id}")
        public String updateReservation(@PathVariable("id") int id, @ModelAttribute("reservation") @Valid Reservation reservation, BindingResult result, Model model) {

            // Валидация
            reservationValidator.validate(reservation, result);


            if (result.hasErrors()) {
                // Добавляем список клиентов и комнат в модель
                model.addAttribute("rooms", roomsService.getRooms());
                return "reservations/edit";
            }


            reservationsService.updateReservation(id, reservation);
            return "redirect:/reservations/" + id;
        }

        @DeleteMapping("/{id}")
        public String deleteReservation(@PathVariable("id") int id) {
            reservationsService.deleteReservation(id);
            return "redirect:/reservations";
        }
    }
