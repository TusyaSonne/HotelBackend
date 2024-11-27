package org.example.HotelBackend.util;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        // Если ID не указан, значит это новый пользователь
        if (person.getId() > 0) {
            // Получаем текущего пользователя по ID
            Person existingPerson = peopleService.getPerson(person.getId());

            if (existingPerson != null) {
                // Проверяем email только если он изменен
                if (!person.getEmail().equals(existingPerson.getEmail()) &&
                        peopleService.findUserByEmail(person.getEmail()).isPresent()) {
                    errors.rejectValue("email", "", "Этот почтовый адрес уже занят.");
                }

                // Проверяем телефон только если он изменен
                if (!person.getPhone().equals(existingPerson.getPhone()) &&
                        peopleService.findUserByPhone(person.getPhone()).isPresent()) {
                    errors.rejectValue("phone", "", "Этот номер телефона уже занят.");
                }
            }
        } else {
            // Проверка для нового пользователя
            if (peopleService.findUserByEmail(person.getEmail()).isPresent()) {
                errors.rejectValue("email", "", "Этот почтовый адрес уже занят.");
            }

            if (peopleService.findUserByPhone(person.getPhone()).isPresent()) {
                errors.rejectValue("phone", "", "Этот номер телефона уже занят.");
            }
        }
    }
}
