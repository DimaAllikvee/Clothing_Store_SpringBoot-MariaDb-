package org.example.services;

import jakarta.transaction.Transactional;
import org.example.apphelpers.CustomerAppHelper;
import org.example.Repository.ClothesRepository;
import org.example.Repository.CustomerRepository;
import org.example.interfaces.Service;
import org.example.model.Clothes;
import org.example.model.Customer;
import org.example.model.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class CustomerService implements Service<Customer> {

    private final CustomerAppHelper customerAppHelper;
    private final CustomerRepository customerRepository;
    private final ClothesRepository clothesRepository;

    @Autowired
    public CustomerService(CustomerAppHelper customerAppHelper, CustomerRepository customerRepository, ClothesRepository clothesRepository) {
        this.customerAppHelper = customerAppHelper;
        this.customerRepository = customerRepository;
        this.clothesRepository = clothesRepository;
    }

    @Override
    public boolean add() {
        Customer customer = customerAppHelper.create();
        if (customer != null) {
            customerRepository.save(customer);
            System.out.println("Клиент добавлен: " + customer);
            return true;
        }
        System.out.println("Ошибка: не удалось добавить клиента.");
        return false;
    }

    @Override
    public boolean add(Customer customer) {
        if (customer != null) {
            customerRepository.save(customer);
            System.out.println("Клиент добавлен через REST API: " + customer);
            return true;
        }
        System.out.println("Ошибка: не удалось добавить клиента через REST API.");
        return false;
    }

    @Override
    public boolean edit(Customer customer) {
        if (customer == null || customer.getId() == null) {
            System.out.println("Ошибка: Указанный клиент или ID отсутствует.");
            return false;
        }

        return customerRepository.findById(customer.getId()).map(existingCustomer -> {
            try {
                // Обновляем поля существующего клиента
                System.out.println("Редактирование клиента:");

                System.out.print("Введите новое имя (текущее: " + existingCustomer.getFirstName() + "): ");
                String newFirstName = customerAppHelper.getInput().getString();
                existingCustomer.setFirstName(newFirstName.isEmpty() ? existingCustomer.getFirstName() : newFirstName);

                System.out.print("Введите новую фамилию (текущая: " + existingCustomer.getLastName() + "): ");
                String newLastName = customerAppHelper.getInput().getString();
                existingCustomer.setLastName(newLastName.isEmpty() ? existingCustomer.getLastName() : newLastName);

                System.out.print("Введите новый баланс (текущий: " + existingCustomer.getBalance() + "): ");
                String newBalanceInput = customerAppHelper.getInput().getString();
                if (!newBalanceInput.isEmpty()) {
                    double newBalance = Double.parseDouble(newBalanceInput);
                    existingCustomer.setBalance(newBalance);
                }

                // Сохраняем обновленного клиента
                customerRepository.save(existingCustomer);
                System.out.println("Ошибка при редактировании клиента: " + existingCustomer);
                return true;
            } catch (Exception e) {
                System.out.println("Клиент успешно обновлён: " + e.getMessage());
                return false;
            }
        }).orElseGet(() -> {
            System.out.println("Ошибка: Клиент с ID " + customer.getId() + " не найден.");
            return false;
        });
    }


    @Override
    public boolean remove(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            System.out.println("Клиент с ID " + id + " успешно удалён.");
            return true;
        } else {
            System.out.println("Ошибка: Клиент с ID " + id + " не найден.");
            return false;
        }
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public boolean print() {
        List<Customer> customerList = customerRepository.findAll();
        return customerAppHelper.printList(customerList);
    }

    @Override
    public List<Customer> list() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public boolean placeOrder(Customer customer) {
        if (customer == null) {
            System.out.println("Ошибка: Клиент отсутствует.");
            return false;
        }

        System.out.println("Оформление заказа для клиента: " + customer.getFirstName() + " " + customer.getLastName());

        // Получаем доступную одежду
        List<Clothes> availableClothes = clothesRepository.findAll();
        if (availableClothes.isEmpty()) {
            System.out.println("Нет доступной одежды для заказа.");
            return false;
        }

        // Печатаем список одежды
        System.out.println("Доступная одежда:");
        for (int i = 0; i < availableClothes.size(); i++) {
            Clothes clothes = availableClothes.get(i);
            System.out.printf("%d. Название: %s, Тип: %s, Размер: %s, Цена: %.2f%n",
                    i + 1, clothes.getName(), clothes.getType(), clothes.getSize(), clothes.getPrice());
        }

        System.out.print("Введите номера одежды для добавления в заказ (через запятую): ");
        String input = customerAppHelper.getInput().getString();
        String[] indices = input.split(",");

        // Создаем заказы для выбранной одежды
        List<Order> orders = new ArrayList<>();
        for (String index : indices) {
            try {
                int itemIndex = Integer.parseInt(index.trim()) - 1;
                if (itemIndex >= 0 && itemIndex < availableClothes.size()) {
                    Clothes selectedClothes = availableClothes.get(itemIndex);

                    // Создаем объект Order для каждой выбранной одежды
                    Order order = new Order();
                    order.setDescription("Заказ на " + selectedClothes.getName());
                    order.setTotalPrice(selectedClothes.getPrice());
                    order.setOrderDate(java.time.LocalDateTime.now());
                    order.setCustomer(customer);

                    orders.add(order);
                } else {
                    System.out.println("Неверный номер: " + (itemIndex + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод: " + index);
            }
        }

        if (orders.isEmpty()) {
            System.out.println("Заказ не сформирован. Одежда не выбрана.");
            return false;
        }

        // Сохраняем заказы в базе данных
        for (Order order : orders) {
            customer.getOrders().add(order); // Связываем заказ с клиентом
        }

        customerRepository.save(customer); // Сохраняем изменения клиента
        System.out.println("Заказ успешно оформлен для клиента: " + customer.getFirstName() + " " + customer.getLastName());
        return true;
    }
}

