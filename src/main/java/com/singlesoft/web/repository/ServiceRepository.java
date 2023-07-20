package com.singlesoft.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.singlesoft.web.model.Customer;
import com.singlesoft.web.model.ServiceModel;
import com.singlesoft.web.model.User;


public interface ServiceRepository extends JpaRepository<ServiceModel, String> {
	// CountByCustomer and findByCustomer and Customer Id
    int countByCustomer(Customer customer);
    List<ServiceModel> findByCustomer(Customer customer);
    List<ServiceModel> findByCustomer_Id(Long customerId);
    // CountByUser and findByUser
    int countByUser(User user);
    List<ServiceModel> findByUser(User user);
    List<ServiceModel> findByUser_Id(Long userId);
    // To List by User and Customer IDs
    List<ServiceModel> findByUserAndCustomer(User user,Customer customer);
}