package org.flywithme.repository;

import org.flywithme.entity.Trip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAll(Specification<Trip> specification, Pageable pageable);

    List<Trip> findAllByUserId(Long id);

    void deleteAllByDateFromLessThan(String date);
}
