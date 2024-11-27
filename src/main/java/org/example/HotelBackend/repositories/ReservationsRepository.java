package org.example.HotelBackend.repositories;

import org.example.HotelBackend.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
            "AND :currentDate >= r.checkIn AND :currentDate < r.checkOut")
    Optional<Reservation> findCurrentReservationForRoom(@Param("roomId") int roomId,
                                                        @Param("currentDate") LocalDate currentDate);

    public List<Reservation> findByRoomId(int roomId);
}

