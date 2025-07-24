package com.stationmarket.api.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stationmarket.api.customer.model.Customer;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(Long userId);
}