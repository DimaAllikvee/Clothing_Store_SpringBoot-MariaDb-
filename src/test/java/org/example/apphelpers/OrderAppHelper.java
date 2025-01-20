package org.example.apphelpers;

import org.example.interfaces.AppHelper;
import org.example.interfaces.Input;
import org.example.model.Clothes;
import org.example.model.Customer;
import org.example.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderAppHelper implements AppHelper<Order> {

    private final Input input;

    @Autowired
    public OrderAppHelper(Input input) {
        this.input = input;
    }

    /**
     * Метод для создания нового заказа.
     *
     * @param customer Клиент, оформляющий заказ
     * @return Созданный объект Order
     */
    public Order create(Customer customer, Clothes clothes) {
        try {
            System.out.println("Оформление нового заказа для клиента: " + customer.getFirstName() + " " + customer.getLastName());
            System.out.println("Товар: " + clothes.getName() + ", Цена: " + clothes.getPrice());

            System.out.print("Введите описание заказа: ");
            String description = input.getString();

            System.out.print("Введите общую стоимость заказа: ");
            double totalPrice = Double.parseDouble(input.getString());

            // Создаём заказ с текущей датой и временем
            return new Order("Покупка товара: " + clothes.getName(), clothes.getPrice(), LocalDateTime.now(), customer);
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
            return null;
        }
    }

    /**
     * Метод для отображения списка заказов.
     *
     * @param orderList Список заказов
     * @return true, если список заказов не пуст, иначе false
     */
    @Override
    public boolean printList(List<Order> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            System.out.println("Список заказов пуст.");
            return false;
        }

        System.out.println("Список заказов:");
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            System.out.printf("%d. Описание: %s, Итоговая стоимость: %.2f, Дата: %s, Клиент: %s %s%n",
                    i + 1,
                    order.getDescription(),
                    order.getTotalPrice(),
                    order.getOrderDate(),
                    order.getCustomer().getFirstName(),
                    order.getCustomer().getLastName()
            );
        }
        return true;
    }

    /**
     * Заглушка для интерфейса AppHelper: не используется напрямую для Order.
     */
    @Override
    public Order create() {
        throw new UnsupportedOperationException("Use create(Customer customer) instead.");
    }
}
