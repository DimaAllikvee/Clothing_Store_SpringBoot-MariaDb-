package org.example.services;

import org.example.apphelpers.ClothingAppHelper;
import org.example.Repository.ClothesRepository;
import org.example.interfaces.Service;
import org.example.model.Clothes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ClothingService implements Service<Clothes> {

    private final ClothingAppHelper clothingAppHelper;
    private final ClothesRepository clothesRepository;

    @Autowired
    public ClothingService(ClothingAppHelper clothingAppHelper, ClothesRepository clothesRepository) {
        this.clothingAppHelper = clothingAppHelper;
        this.clothesRepository = clothesRepository;
    }

    @Override
    public boolean add() {
        Clothes clothes = clothingAppHelper.create();
        if (clothes != null) {
            clothesRepository.save(clothes);
            System.out.println("Одежда добавлена: " + clothes);
            return true;
        }
        System.out.println("Ошибка: не удалось добавить одежду.");
        return false;
    }

    @Override
    public boolean add(Clothes clothes) {
        if (clothes != null) {
            clothesRepository.save(clothes);
            System.out.println("Одежда добавлена через REST API: " + clothes);
            return true;
        }
        System.out.println("Ошибка: не удалось добавить одежду через REST API.");
        return false;
    }

    @Override
    public boolean edit(Clothes clothes) {
        if (clothes == null || clothes.getId() == null) {
            System.out.println("Ошибка: Указанная одежда или ID отсутствует.");
            return false;
        }

        return clothesRepository.findById(clothes.getId()).map(existingClothes -> {
            try {
                System.out.println("Редактирование одежды:");
                System.out.print("Введите новое название (текущее: " + existingClothes.getName() + "): ");
                String newName = clothingAppHelper.getInput().getString();
                existingClothes.setName(newName.isEmpty() ? existingClothes.getName() : newName);

                System.out.print("Введите новый тип одежды (текущий: " + existingClothes.getType() + "): ");
                String newType = clothingAppHelper.getInput().getString();
                existingClothes.setType(newType.isEmpty() ? existingClothes.getType() : newType);

                System.out.print("Введите новый размер одежды (текущий: " + existingClothes.getSize() + "): ");
                String newSize = clothingAppHelper.getInput().getString();
                existingClothes.setSize(newSize.isEmpty() ? existingClothes.getSize() : newSize);

                System.out.print("Введите новый цвет одежды (текущий: " + existingClothes.getColor() + "): ");
                String newColor = clothingAppHelper.getInput().getString();
                existingClothes.setColor(newColor.isEmpty() ? existingClothes.getColor() : newColor);

                System.out.print("Введите новую цену одежды (текущая: " + existingClothes.getPrice() + "): ");
                String newPrice = clothingAppHelper.getInput().getString();
                if (!newPrice.isEmpty()) {
                    existingClothes.setPrice(Double.parseDouble(newPrice));
                }

                System.out.print("Введите новое количество одежды (текущее: " + existingClothes.getQuantity() + "): ");
                String newQuantity = clothingAppHelper.getInput().getString();
                if (!newQuantity.isEmpty()) {
                    existingClothes.setQuantity(Integer.parseInt(newQuantity));
                }

                clothesRepository.save(existingClothes);
                System.out.println("Одежда успешно обновлена: " + existingClothes);
                return true;
            } catch (Exception e) {
                System.out.println("Ошибка при редактировании одежды: " + e.getMessage());
                return false;
            }
        }).orElseGet(() -> {
            System.out.println("Ошибка: Одежда с ID " + clothes.getId() + " не найдена.");
            return false;
        });
    }

    @Override
    public boolean remove(Long id) {
        if (clothesRepository.existsById(id)) {
            clothesRepository.deleteById(id);
            System.out.println("Одежда с ID " + id + " успешно удалена.");
            return true;
        } else {
            System.out.println("Ошибка: Одежда с ID " + id + " не найдена.");
            return false;
        }
    }

    @Override
    public Clothes findById(Long id) {
        return clothesRepository.findById(id).orElse(null);
    }

    @Override
    public boolean print() {
        List<Clothes> clothesList = clothesRepository.findAll();
        return clothingAppHelper.printList(clothesList);
    }

    @Override
    public List<Clothes> list() {
        return clothesRepository.findAll();
    }
}
