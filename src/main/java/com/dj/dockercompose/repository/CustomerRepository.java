package com.dj.dockercompose.repository;

import com.dj.dockercompose.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {
}