package org.example.HotelBackend.services;

import org.example.HotelBackend.models.Reservation;
import org.example.HotelBackend.models.Room;
import org.example.HotelBackend.repositories.ReservationsRepository;
import org.example.HotelBackend.repositories.RoomsRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RoomsService {

    private final RoomsRepository roomsRepository;
    private final ReservationsRepository reservationsRepository;

    public RoomsService(RoomsRepository roomsRepository, ReservationsRepository reservationsRepository) {
        this.roomsRepository = roomsRepository;
        this.reservationsRepository = reservationsRepository;
    }

    public List<Room> getRooms() {
        return roomsRepository.findAll();
    }

    public Room getRoom(int id) {
        return roomsRepository.findById(id).orElse(null);
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addRoom(Room room) {
        roomsRepository.save(room);
    }

    @Transactional
    public void updateRoom(int id, Room updatedRoom) {
        updatedRoom.setId(id);
        roomsRepository.save(updatedRoom);
    }

    @Transactional
    public void deleteRoom(int id) {
        roomsRepository.deleteById(id);
    }

    public Optional<Reservation> getCurrentReservation(int roomId) {
        return reservationsRepository.findCurrentReservationForRoom(roomId, LocalDate.now());
    }

    public Map<Integer, Boolean> getRoomsOccupancy() {

        List<Room> rooms = roomsRepository.findAll();
        // список для хранения информации о занятости номеров
        Map<Integer,Boolean> roomOccupancy = new HashMap<>();

        // Для каждого номера проверяем, есть ли активное бронирование
        for (Room room : rooms) {
            Optional<Reservation> reservation = getCurrentReservation(room.getId());
            roomOccupancy.put(room.getId(), reservation.isPresent());
        }

        return roomOccupancy;
    }
}
