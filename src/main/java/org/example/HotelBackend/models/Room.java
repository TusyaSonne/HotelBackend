package org.example.HotelBackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "capacity")
    @NotNull
    @Min(value = 2, message = "Вместимость не может быть меньше 2")
    @Max(value = 8, message = "Вместимость не может быть больше 8")
    private int capacity;

    @Column(name = "price_per_night")
    @Min(value = 0, message = "Сумма не может быть меньше 0")
    private float pricePerNight;

    @OneToMany(mappedBy = "room") //, fetch = FetchType.EAGER
    private List<Reservation> reservations;

    public Room() {}

    public Room(int id, int capacity, float pricePerNight, List<Reservation> reservations) {
        this.id = id;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.reservations = reservations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(float pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
