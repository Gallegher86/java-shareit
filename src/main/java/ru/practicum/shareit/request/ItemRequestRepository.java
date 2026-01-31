package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select r from ItemRequest r " +
            "left join fetch r.items " +
            "where r.requestor.id = :userId " +
            "order by r.created desc")
    List<ItemRequest> findByRequestorIdWithItems(@Param("userId") Long userId);

    @Query("select r from ItemRequest r " +
            "left join fetch r.items " +
            "where r.id = :id ")
    Optional<ItemRequest> findByIdWithItems(@Param("id") Long id);

    @Query("select new ru.practicum.shareit.request.dto.ItemRequestDto" +
            "(r.id, r.description, r.created) " +
            "from ItemRequest r " +
            "where r.requestor.id <> :userId " +
            "order by r.created desc")
    List<ItemRequestDto> findAllExceptUser(@Param("userId") Long userId);
}
