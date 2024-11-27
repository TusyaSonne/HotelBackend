package org.example.HotelBackend.repositories;

import org.example.HotelBackend.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomsRepository extends JpaRepository<Room, Integer> {
}
