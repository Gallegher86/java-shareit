package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where b.id = :id " +
            "and b.status = :status " +
            "and i.owner.id = :ownerId"
    )
    Optional<Booking> findByIdItemOwnerAndStatus(@Param("id") Long id, @Param("ownerId") Long ownerId,
                                                 @Param("status") BookingStatus status);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where b.id = :id " +
            "and (i.owner.id = :userId or b.booker.id = :userId)"
    )
    Optional<Booking> findByIdItemOwnerOrBooker(@Param("id") Long id, @Param("userId") Long userId);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where ((:role = 'BOOKER' and b.booker.id = :userId) " +
            "or (:role = 'OWNER' and i.owner.id = :userId))"
    )
    Page<Booking> findAllByItemOwnerOrBooker(@Param("userId") Long userId, @Param("role") String role,
                                             Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where ((:role = 'BOOKER' and b.booker.id = :userId) " +
            "or (:role = 'OWNER' and i.owner.id = :userId)) " +
            "and (b.start <= :now and b.end >= :now)"
    )
    Page<Booking> findAllStateCurrent(@Param("userId") Long userId, @Param("now") LocalDateTime now,
                                      @Param("role") String role, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where ((:role = 'BOOKER' and b.booker.id = :userId) " +
            "or (:role = 'OWNER' and i.owner.id = :userId)) " +
            "and (b.end < :now)"
    )
    Page<Booking> findAllStatePast(@Param("userId") Long userId, @Param("now") LocalDateTime now,
                                   @Param("role") String role, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where ((:role = 'BOOKER' and b.booker.id = :userId) " +
            "or (:role = 'OWNER' and i.owner.id = :userId)) " +
            "and (b.start > :now)"
    )
    Page<Booking> findAllStateFuture(@Param("userId") Long userId, @Param("now") LocalDateTime now,
                                     @Param("role") String role, Pageable pageable);

    @Query(" select b " +
            "from Booking b " +
            "join b.item i " +
            "where ((:role = 'BOOKER' and b.booker.id = :userId) " +
            "or (:role = 'OWNER' and i.owner.id = :userId)) " +
            "and b.status = :status"
    )
    Page<Booking> findByItemOwnerOrBookerAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status,
                                                   @Param("role") String role, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id in :itemIds " +
            "and b.status = 'APPROVED' " +
            "and b.end < :now " +
            "order by b.end desc")
    List<Booking> findItemsLastBookings(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id in :itemIds " +
            "and b.status = 'APPROVED' " +
            "and b.start > :now " +
            "order by b.end desc")
    List<Booking> findItemsNextBookings(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);
}

