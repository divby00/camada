package org.wildcat.camada.service;

public interface PersistenceService<T> {
    void saveEntity(T rowValue);

    T find(Long id);
}
