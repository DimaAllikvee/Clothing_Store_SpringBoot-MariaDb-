package org.example.services;

import org.example.model.Clothes;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.Repository.ClothesRepository;
import org.example.Repository.CustomerRepository;
import org.example.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ClothesRepository clothesRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ClothesRepository clothesRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.clothesRepository = clothesRepository;
    }

    @Transactional
    public boolean placeOrder(Long customerId, Order order, Long clothesId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Optional<Clothes> optionalClothes = clothesRepository.findById(clothesId);

        if (optionalCustomer.isEmpty()) {
            System.out.println("Ошибка: Клиент с ID " + customerId + " не найден.");
            return false;
        }

        if (optionalClothes.isEmpty()) {
            System.out.println("Ошибка: Товар с ID " + clothesId + " не найден.");
            return false;
        }

        Clothes clothes = optionalClothes.get();
        Customer customer = optionalCustomer.get();

        // Проверка наличия товара
        if (clothes.getQuantity() <= 0) {
            System.out.println("Ошибка: Товара " + clothes.getName() + " нет в наличии.");
            return false;
        }

        // Проверка баланса клиента
        if (customer.getBalance() < clothes.getPrice()) {
            System.out.println("Ошибка: У клиента недостаточно средств для покупки товара.");
            return false;
        }

        // Обновляем данные клиента и товара
        customer.setBalance(customer.getBalance() - clothes.getPrice());
        clothes.setQuantity(clothes.getQuantity() - 1);

        // Сохраняем изменения в базе данных
        customerRepository.save(customer);
        clothesRepository.save(clothes);

        // Настройка и сохранение заказа
        order.setCustomer(customer);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setTotalPrice(clothes.getPrice());
        order.setDescription("Покупка товара: " + clothes.getName());

        orderRepository.save(order);

        System.out.println("Заказ успешно оформлен для клиента: " + customer.getFirstName() + " " + customer.getLastName());
        return true;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            System.out.println("Ошибка: Клиент с ID " + customerId + " не найден.");
            return new ArrayList<>();
        }
        return orderRepository.findByCustomer(optionalCustomer.get());
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public boolean removeOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            System.out.println("Ошибка: Заказ с ID " + orderId + " не найден.");
            return false;
        }

        orderRepository.deleteById(orderId);
        System.out.println("Заказ с ID " + orderId + " успешно удален.");
        return true;
    }

    @Transactional(readOnly = true)
    public void printAllOrders() {
        List<Order> orders = getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("Заказов нет.");
            return;
        }

        System.out.println("Все заказы:");
        for (Order order : orders) {
            System.out.printf("ID заказа: %d, Описание: %s, Цена: %.2f, Дата: %s, Клиент: %s%n",
                    order.getId(),
                    order.getDescription(),
                    order.getTotalPrice(),
                    order.getOrderDate(),
                    order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        }
    }

    @Transactional(readOnly = true)
    public void printOrdersByCustomer(Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isEmpty()) {
            System.out.println("Клиент с ID " + customerId + " не найден.");
            return;
        }

        List<Order> orders = getOrdersByCustomer(customerId);
        if (orders.isEmpty()) {
            System.out.println("У клиента с ID " + customerId + " нет заказов.");
            return;
        }

        System.out.println("Заказы клиента " + optionalCustomer.get().getFirstName() + " " + optionalCustomer.get().getLastName() + ":");
        for (Order order : orders) {
            System.out.printf("ID заказа: %d, Описание: %s, Цена: %.2f, Дата: %s%n",
                    order.getId(),
                    order.getDescription(),
                    order.getTotalPrice(),
                    order.getOrderDate());
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOrdersWithCustomerDetails(Long customerId) {
        List<Order> orders = getOrdersByCustomer(customerId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : orders) {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.getId());
            orderDetails.put("description", order.getDescription());
            orderDetails.put("totalPrice", order.getTotalPrice());
            orderDetails.put("orderDate", order.getOrderDate());
            orderDetails.put("customerName", order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
            result.add(orderDetails);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Double calculateIncomeForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(23, 59, 59);
        return orderRepository.getIncomeBetweenDates(startOfDay, endOfDay);
    }

    @Transactional(readOnly = true)
    public Double calculateIncomeForMonth(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        return orderRepository.getIncomeBetweenDates(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
    }

    @Transactional(readOnly = true)
    public Double calculateIncomeForYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return orderRepository.getIncomeBetweenDates(startOfYear.atStartOfDay(), endOfYear.atTime(23, 59, 59));
    }
}
