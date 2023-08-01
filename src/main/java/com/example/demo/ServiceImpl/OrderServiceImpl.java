package com.example.demo.ServiceImpl;

import com.example.demo.Entity.Order;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepo;

    public OrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }


    @Override
    public void savedOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public List<Order> getOrderById(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByBookId(Long bookId) {
        return orderRepo.findOrdersByBookId(bookId);
    }

    @Override
    public void deleteOrderById(long id) {
        orderRepo.deleteById(id);
    }
}
