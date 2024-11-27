package org.example.HotelBackend.util;

import org.example.HotelBackend.models.Reservation;
import org.example.HotelBackend.services.ReservationsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationValidator implements Validator {

    private final ReservationsService reservationsService;

    public ReservationValidator(ReservationsService reservationsService) {
        this.reservationsService = reservationsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Reservation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Reservation reservation = (Reservation) target;

        if (reservation.getCheckIn() == null) {
            errors.rejectValue("checkIn", "required.checkIn", "Необходимо заполнить дату въезда");
        }

        if (reservation.getCheckOut() == null) {
            errors.rejectValue("checkOut", "required.checkOut", "Необходимо заполнить дату выезда");
        }

        // Прерывание проверки, если даты отсутствуют
        if (reservation.getCheckIn() == null || reservation.getCheckOut() == null) {
            return; // Если даты отсутствуют, дальнейшая проверка невозможна
        }

        // Проверка на прошедшие даты
        if (reservation.getCheckIn().isBefore(LocalDate.now())) {
            errors.rejectValue("checkIn", "invalid.checkIn", "Дата въезда недоступна.");
        }

        // Проверка на совпадение дат
        if (reservation.getCheckIn().isEqual(reservation.getCheckOut())) {
            errors.rejectValue("checkIn", "invalid.checkIn", "Дата въезда не может совпадать с датой выезда.");
        }

        if (reservation.getCheckOut().isBefore(reservation.getCheckIn())) {
            errors.rejectValue("checkOut", "invalid.checkOut", "Дата выезда не может идти до даты въезда");
        }

        // Проверка на пересечение с уже существующими бронированиями
        List<Reservation> existingReservations = reservationsService.getReservationsByRoom(reservation.getRoom().getId());

        // Учитываем текущее бронирование, если оно уже существует
        for (Reservation existing : existingReservations) {
            // Пропускаем текущее бронирование
            if (reservation.getId() != 0 && reservation.getId() == existing.getId()) {
                continue;
            }

            // Проверяем пересечение дат
            boolean overlap = reservation.getCheckIn().isBefore(existing.getCheckOut()) &&
                    reservation.getCheckOut().isAfter(existing.getCheckIn());
            if (overlap) {
                errors.rejectValue("checkIn", "conflict.dates", "Номер уже забронирован на данные даты");
                break;
            }
        }
    }
}