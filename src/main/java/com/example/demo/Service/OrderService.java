package com.example.demo.Service;

import com.example.demo.Entity.Order;

import java.util.List;

public interface OrderService {
    void savedOrder(Order order);
    List<Order> getOrderById(Long id);
    List<Order> getOrdersByBookId(Long bookId);
    void deleteOrderById(long id);
}
