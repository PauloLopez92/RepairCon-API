package com.singlesoft.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.singlesoft.web.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}