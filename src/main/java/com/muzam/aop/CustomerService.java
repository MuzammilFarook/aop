package com.muzam.aop;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Auditable(action = "CREATE", entityName = "Customer")
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Auditable(action = "UPDATE", entityName = "Customer")
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Auditable(action = "DELETE", entityName = "Customer")
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
