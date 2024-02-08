package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    Collection<ItemEntity> findUsersByOwnerIdOrderByIdAsc(Integer ownerId);

    @Query("select i from ItemEntity i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<ItemEntity> search(String text);
}