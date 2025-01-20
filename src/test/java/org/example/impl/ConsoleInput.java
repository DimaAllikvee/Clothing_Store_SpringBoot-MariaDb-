package org.example.impl;

import org.example.interfaces.Input;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleInput implements Input {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String getString() {
        return scanner.nextLine();
    }

    @Override
    public int getInt() {
        while (true) {
            try {
                return Integer.parseInt(getString());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число:");
            }
        }
    }
}
