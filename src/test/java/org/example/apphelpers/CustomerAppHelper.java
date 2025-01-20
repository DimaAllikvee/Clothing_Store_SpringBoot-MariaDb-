package org.example.apphelpers;

import org.example.interfaces.AppHelper;
import org.example.interfaces.Input;
import org.example.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerAppHelper implements AppHelper<Customer> {

    private final Input input;

    @Autowired
    public CustomerAppHelper(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return this.input;
    }

    @Override
    public Customer create() {
        try {
            System.out.print("Введите имя клиента: ");
            String firstName = input.getString();
            System.out.print("Введите фамилию клиента: ");
            String lastName = input.getString();
            System.out.print("Введите баланс клиента: ");
            double balance = Double.parseDouble(input.getString());
            return new Customer(firstName, lastName, balance); // Учитываем баланс
        } catch (Exception e) {
            System.out.println("Ошибка при создании клиента: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean printList(List<Customer> customerList) {
        if (customerList == null || customerList.isEmpty()) {
            System.out.println("Список клиентов пуст.");
            return false;
        }

        for (int i = 0; i < customerList.size(); i++) {
            Customer customer = customerList.get(i);
            System.out.printf("%d. Имя: %s, Фамилия: %s, Баланс: %.2f%n",
                    i + 1,
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getBalance()); // Отображение баланса
        }
        return true;
    }
}
