package org.example.HotelBackend.services;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.repositories.PeopleRepository;
import org.example.HotelBackend.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;


    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByEmail(username);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User with this email not found");
        }

        return new PersonDetails(person.get());
    }
}
