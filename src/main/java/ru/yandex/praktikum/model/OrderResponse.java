package ru.yandex.praktikum.model;

public class OrderResponse {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "order=" + order +
                '}';
    }
}
