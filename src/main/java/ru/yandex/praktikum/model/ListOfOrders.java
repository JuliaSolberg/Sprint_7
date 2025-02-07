package ru.yandex.praktikum.model;

import java.util.List;

public class ListOfOrders {
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "ListOfOrders{" +
                "orders=" + orders +
                '}';
    }
}
