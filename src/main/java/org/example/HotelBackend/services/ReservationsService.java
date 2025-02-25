package org.example.HotelBackend.services;

import org.example.HotelBackend.models.Person;
import org.example.HotelBackend.models.Reservation;
import org.example.HotelBackend.repositories.ReservationsRepository;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@EnableAsync
public class ReservationsService {

    private final ReservationsRepository reservationsRepository;
    private final EmailService emailService;

    public ReservationsService(ReservationsRepository reservationsRepository, EmailService emailService) {
        this.reservationsRepository = reservationsRepository;
        this.emailService = emailService;
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

        // Отправка email после создания брони
        String email = reservation.getOwner().getEmail();
        String subject = "Подтверждение бронирования";
        String content = String.format(
                "Здравствуйте, %s!\n\nВы успешно забронировали номер в отеле.\n" +
                        "Дата заезда: %s\nДата выезда: %s\nИтоговая стоимость: %.2f руб.\n\nСпасибо за выбор нашего отеля!",
                reservation.getOwner().getFullName(), reservation.getCheckIn(), reservation.getCheckOut(), reservation.getTotalPrice()
        );
        emailService.sendMail(email, subject, content);
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
