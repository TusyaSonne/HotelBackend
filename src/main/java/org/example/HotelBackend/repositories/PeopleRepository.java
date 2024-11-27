package org.example.HotelBackend.repositories;

import org.example.HotelBackend.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    public Optional<Person> findByEmail(String email);

    public Optional<Object> findByPhone(String phone);
}
