package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {
}