package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, QuerydslPredicateExecutor<ItemRequest> {

    List<ItemRequest> findByRequestorId(long requestorId, Sort sort);

    Optional<ItemRequest> findByRequestorIdAndId(Long requestorId, Long id);
}
