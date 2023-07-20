package com.singlesoft.web.controller;

import java.util.List;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.singlesoft.web.model.Customer;
import com.singlesoft.web.model.ServiceModel;
import com.singlesoft.web.model.User;
import com.singlesoft.web.repository.CustomerRepository;
import com.singlesoft.web.repository.ServiceRepository;
import com.singlesoft.web.repository.UserRepository;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
	@Autowired
    private UserRepository userRepository;


    @GetMapping
    public List<Customer> getAllCustomers() {
    	// To set Customer NumServices
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            int numServices = serviceRepository.countByCustomer(customer);
            customer.setNumServices(numServices);
        }
        return customers;
    }

	@GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()) {
            int numServices = serviceRepository.countByCustomer(customer.get());
            customer.get().setNumServices(numServices);
            return ResponseEntity.ok().body(customer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

	@PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable(value = "id") Long customerId,
                                      @Valid @RequestBody Customer customerDetails) {
		Optional<Customer> customerop = customerRepository.findById(customerId);
        
		if(customerop.isPresent()) {
			Customer customer = customerop.get();
	        customer.setName(customerDetails.getName());
			customer.setContact(customerDetails.getContact());
			customer.setAddress(customerDetails.getAddress());

			Customer customerUpdate = customerRepository.save(customer);
			return customerUpdate;		
		}else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
		}

    }

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCustomer(HttpServletRequest request,@PathVariable Long id) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);

        if (!currentUser.getUserType().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
        // Delete Function
		Optional<Customer> customer = customerRepository.findById(id);
		if (customer.isPresent()) {

        List<ServiceModel> serviceList =  serviceRepository.findByCustomer(customer.get());
        for(ServiceModel service : serviceList) {
        	serviceRepository.delete(service);
        }

		customerRepository.delete(customer.get());

		return ResponseEntity.ok().body("Customer deleted successfully.");
		} else {
		return ResponseEntity.notFound().build();
		}
	}

}
