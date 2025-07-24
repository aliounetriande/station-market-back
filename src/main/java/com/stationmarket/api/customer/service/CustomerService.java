package com.stationmarket.api.customer.service;

import org.springframework.stereotype.Service;
import com.stationmarket.api.customer.model.Customer;
import com.stationmarket.api.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> findAll() { return customerRepository.findAll(); }
    public Optional<Customer> findById(Long id) { return customerRepository.findById(id); }
    public Optional<Customer> findByUserId(Long userId) { return customerRepository.findByUserId(userId); }
    public Customer save(Customer customer) { return customerRepository.save(customer); }
    public void deleteById(Long id) { customerRepository.deleteById(id); }
}
