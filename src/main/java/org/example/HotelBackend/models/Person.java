package org.example.HotelBackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.lang.NonNull;

import java.util.List;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    @NotEmpty(message = "ФИО не может быть пустым")
    private String fullName;

    @NotNull(message = "Пароль не может быть пустым")
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    @NotEmpty(message = "Почта не может быть пустой")
    @Email(message = "Почта должна быть валидна")
    private String email;

    @Column(name = "phone")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Введите телефон в виде +78005553535")
    private String phone;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Reservation> reservations;

    public Person() {}

    public Person(int id, String fullName, String password, String email,
                  String phone, String role, List<Reservation> reservations) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.reservations = reservations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
