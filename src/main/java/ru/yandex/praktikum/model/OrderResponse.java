package ru.yandex.praktikum.model;

public class OrderResponse {
    private Order order;

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "order=" + order +
                '}';
    }
}
