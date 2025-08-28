package com.stationmarket.api.payments.repository;

import com.stationmarket.api.payments.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, String> {
}