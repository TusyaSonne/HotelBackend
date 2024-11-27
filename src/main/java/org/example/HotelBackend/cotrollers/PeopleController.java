package org.example.HotelBackend.cotrollers;

import jakarta.validation.Valid;
import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.security.PersonDetails;
import org.example.HotelBackend.services.PeopleService;
import org.example.HotelBackend.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }


    @GetMapping()
    public String showPeople(Model model) {

        model.addAttribute("people", peopleService.getPeople());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String showPersonById(@PathVariable("id") int id, Model model) {
        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("role", currentUser.getRole());

        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("person", peopleService.getPerson(id));
        } else {
            if (currentUser.getId() != id) {
                return "people/error";
            }

            model.addAttribute("person", peopleService.getPerson(id));
        }
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person, Model model) {

        //Добавление ролей для выбора
        List<String> roles = Arrays.asList("ROLE_CLIENT", "ROLE_ADMIN");
        model.addAttribute("roles", roles);

        return "people/add";
    }

    @PostMapping()
    public String addPerson(@ModelAttribute("person") @Valid Person person, BindingResult result, Model model) {

        personValidator.validate(person, result);

        if (result.hasErrors()) {
            //Добавление ролей для выбора
            List<String> roles = Arrays.asList("ROLE_CLIENT", "ROLE_ADMIN");
            model.addAttribute("roles", roles);
            return "people/add";
        }

        peopleService.addPerson(person);
        return "redirect:/people/" + person.getId();
    }

    @GetMapping("/{id}/edit")
    public String editPerson(@PathVariable("id") int id, Model model) {
        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("person", peopleService.getPerson(id));
            //Добавление ролей для выбора
            List<String> roles = Arrays.asList("ROLE_CLIENT", "ROLE_ADMIN");
            model.addAttribute("roles", roles);

        } else {
            if (currentUser.getId() != id) {
                return "people/error";
            }

            model.addAttribute("person", peopleService.getPerson(id));
        }

        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String updatePerson(@PathVariable("id") int id, @ModelAttribute("person") @Valid Person person, BindingResult result, Model model) {

        personValidator.validate(person, result);

        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        Person existingPerson = peopleService.getPerson(id);
        person.setPassword(existingPerson.getPassword());


        if (result.hasErrors()) {
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                List<String> roles = Arrays.asList("ROLE_CLIENT", "ROLE_ADMIN");
                model.addAttribute("roles", roles);
            }
            return "people/edit";

        }

        peopleService.updatePerson(id, person);
        return "redirect:/people/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id, Model model) {
        Person currentUser = peopleService.getCurrentUser();
        model.addAttribute("role", currentUser.getRole());

        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            peopleService.deletePerson(id);
        } else {
            if (currentUser.getId() != id) {
                return "people/error";
            }
            peopleService.deletePerson(id);
        }
        peopleService.deletePerson(id);

        return "people/success_delete";
    }

    @GetMapping("/{id}/change-password")
    public String changePasswordForm(@PathVariable("id") int id, Model model) {

        model.addAttribute("id", id); // Для передачи ID в форму
        return "people/change_password";
    }

    @PostMapping("/{id}/change-password")
    public String changePassword(@PathVariable("id") int id,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {

        Person person = peopleService.getPerson(id);

        // Проверка текущего пароля
        if (!peopleService.checkPassword(person, currentPassword)) {
            model.addAttribute("error", "Текущий пароль указан неверно.");
            return "people/change_password";
        }

        // Проверка совпадения нового пароля и подтверждения
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Новый пароль и подтверждение пароля не совпадают.");
            return "people/change_password";
        }

        // Проверка, что новый пароль отличается от текущего
        if (peopleService.checkPassword(person, newPassword)) {
            model.addAttribute("error", "Новый пароль должен отличаться от текущего.");
            return "people/change_password";
        }

        // Сохранение нового пароля
        peopleService.updatePassword(person, newPassword);
        return "redirect:/people/" + id;
    }

}
