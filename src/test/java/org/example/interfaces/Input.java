package org.example.interfaces;
import java.util.Scanner;



public interface Input {

    default String getString() {
        return new Scanner(System.in).nextLine();
    }

    default int getInt() {
        while (true) {
            try {
                return Integer.parseInt(getString());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное целое число:");
            }
        }
    }

    default double getDouble() {
        while (true) {
            try {
                return Double.parseDouble(getString());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число с плавающей точкой:");
            }
        }
    }
}
