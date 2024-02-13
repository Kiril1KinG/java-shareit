package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    Collection<ItemEntity> findAllByOwnerIdOrderByIdAsc(Integer ownerId);

    Page<ItemEntity> findAllByOwnerId(Integer ownerId, Pageable pageable);

    @Query("select i from ItemEntity i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))) " +
            "and i.available = true")
    List<ItemEntity> search(@Param("text") String text);

    @Query("select i from ItemEntity i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))) " +
            "and i.available = true")
    Page<ItemEntity> search(@Param("text") String text, Pageable pageable);

    Collection<ItemEntity> findAllByRequestRequestorId(Integer userId);

    Collection<ItemEntity> findAllByRequestId(Integer requestId);
}