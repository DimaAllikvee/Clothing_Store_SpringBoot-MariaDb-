package org.example.interfaces;

import java.util.List;

public interface Service<T> {
    boolean add();
    boolean add(T entity);
    boolean edit(T entity);
    boolean remove(Long id);
    T findById(Long id);
    boolean print();
    List<T> list();


    default boolean placeOrder(T entity) {
        throw new UnsupportedOperationException("Метод placeOrder не реализован");
    }
}

