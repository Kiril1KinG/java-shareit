package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.entity.ItemRequestEntity;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Integer> {

    Collection<ItemRequestEntity> findAllByRequestorIdOrderByCreatedDesc(Integer requestorId);

    @Query(value = "SELECT * FROM requests " +
            "WHERE requestor_id != :userId", nativeQuery = true)
    Page<ItemRequestEntity> findAllWithoutRequestor(Integer userId, Pageable pageable);
}