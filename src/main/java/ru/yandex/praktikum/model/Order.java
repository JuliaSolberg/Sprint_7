package ru.yandex.praktikum.model;


import java.util.Arrays;

public class Order {
    private int id;
    private Integer courierId; // может быть null, поэтому Integer
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private int track;
    private String comment;
    private String[] color;
    private String createdAt;
    private String updatedAt;
    private int status;

    public Order() {

    }

    public Order(String firstName, String lastName, String address, String metroStation, String phone,
                 int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", courierId=" + courierId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", metroStation='" + metroStation + '\'' +
                ", phone='" + phone + '\'' +
                ", rentTime=" + rentTime +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", track=" + track +
                ", comment='" + comment + '\'' +
                ", color=" + Arrays.toString(color) +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", status=" + status +
                '}';
    }
}
