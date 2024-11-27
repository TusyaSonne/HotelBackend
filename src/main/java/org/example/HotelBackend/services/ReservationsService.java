package org.example.HotelBackend.services;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.models.Reservation;
import org.example.HotelBackend.repositories.ReservationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationsService {

    private final ReservationsRepository reservationsRepository;

    public ReservationsService(ReservationsRepository reservationsRepository) {
        this.reservationsRepository = reservationsRepository;
    }

    public List<Reservation> getReservations() {
        return reservationsRepository.findAll();
    }

    public Reservation getReservation(int id) {
        return reservationsRepository.findById(id).orElse(null);
    }

    @Transactional
    public void addReservation(Reservation reservation) {
        calculateTotalPrice(reservation);
        reservationsRepository.save(reservation);
    }

    @Transactional
    public void updateReservation(int id, Reservation updatedReservation) {
        updatedReservation.setId(id);
        calculateTotalPrice(updatedReservation);
        reservationsRepository.save(updatedReservation);
    }

    @Transactional
    public void deleteReservation(int id) {
        reservationsRepository.deleteById(id);
    }

    //Рассчет итоговой суммы за бронь
    private void calculateTotalPrice(Reservation reservation) {
        if (reservation.getCheckIn() != null && reservation.getCheckOut() != null && reservation.getRoom() != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut());
            reservation.setTotalPrice(nights * reservation.getRoom().getPricePerNight());
        }
    }

    public List<Reservation> getReservationsByRoom(int id) {
        return reservationsRepository.findByRoomId(id);
    }
}
