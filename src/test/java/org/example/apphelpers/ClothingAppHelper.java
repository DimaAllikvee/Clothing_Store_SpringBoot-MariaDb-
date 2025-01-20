package org.example.apphelpers;

import org.example.interfaces.AppHelper;
import org.example.interfaces.Input;
import org.example.model.Clothes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClothingAppHelper implements AppHelper<Clothes> {

    private final Input input;

    @Autowired
    public ClothingAppHelper(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return this.input;
    }

    /**
     * Метод для создания одежды.
     */
    @Override
    public Clothes create() {
        try {
            System.out.print("Введите название одежды: ");
            String name = input.getString();
            System.out.print("Введите тип одежды (например, футболка, куртка): ");
            String type = input.getString();
            System.out.print("Введите размер одежды (например, S, M, L, XL): ");
            String size = input.getString();
            System.out.print("Введите цвет одежды: ");
            String color = input.getString();
            System.out.print("Введите цену одежды: ");
            double price = Double.parseDouble(input.getString());
            System.out.print("Введите количество одежды: ");
            int quantity = Integer.parseInt(input.getString());

            // Создаем и возвращаем объект Clothes с количеством
            Clothes clothes = new Clothes(name, type, size, color, price);
            clothes.setQuantity(quantity);
            return clothes;
        } catch (Exception e) {
            System.out.println("Ошибка при создании одежды: " + e.getMessage());
            return null;
        }
    }


    /**
     * Метод для отображения списка одежды.
     */
    @Override
    public boolean printList(List<Clothes> clothesList) {
        if (clothesList == null || clothesList.isEmpty()) {
            System.out.println("Список одежды пуст.");
            return false;
        }

        for (int i = 0; i < clothesList.size(); i++) {
            Clothes clothes = clothesList.get(i);
            System.out.printf("%d. Название: %s, Тип: %s, Размер: %s, Цвет: %s, Цена: $%.2f, Количество: %d%n",
                    i + 1,
                    clothes.getName(),
                    clothes.getType(),
                    clothes.getSize(),
                    clothes.getColor(),
                    clothes.getPrice(),
                    clothes.getQuantity());
        }
        return true;
    }
}

