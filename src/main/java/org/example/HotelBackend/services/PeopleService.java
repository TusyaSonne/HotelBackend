package org.example.HotelBackend.services;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.repositories.PeopleRepository;
import org.example.HotelBackend.security.PersonDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Person> getPeople() {
        return peopleRepository.findAll();
    }

    public Person getPerson(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleRepository.save(person);
    }


    @Transactional
    public void updatePerson(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }


    @Transactional
    public void deletePerson(int id) {
        peopleRepository.deleteById(id);
    }


    public Optional<Person> findUserByEmail(String email) {
        return peopleRepository.findByEmail(email);
    }

    public Optional<Object> findUserByPhone(String phone) {
        return peopleRepository.findByPhone(phone);
    }

    public Person getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        return personDetails.getPerson();
    }

    @Transactional
    public void updatePassword(Person person, String newPassword) {
        person.setPassword(passwordEncoder.encode(newPassword));
        peopleRepository.save(person);
    }

    public boolean checkPassword(Person person, String rawPassword) {
        return passwordEncoder.matches(rawPassword, person.getPassword());
    }

}
