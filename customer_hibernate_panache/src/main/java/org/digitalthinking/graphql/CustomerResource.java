package org.digitalthinking.graphql;

import io.smallrye.mutiny.Uni;
import org.digitalthinking.entites.Customer;
import org.digitalthinking.repositories.CustomerRepository;
import org.eclipse.microprofile.graphql.*;

import javax.inject.Inject;
import java.util.List;

@GraphQLApi
public class CustomerResource {

    @Inject
    private CustomerRepository customerRepository;

    @Query("AllCustomer")
    @Description("Get all customers from a database")
    public Uni<List<Customer>> getAllCustomer() {
        return customerRepository.listAll();
    }

    @Query
    @Description("Get a customer from database")
    public Uni<Customer> getCustomer(@Name("customerId") Long Id){
        return customerRepository.findById(Id);
    }

    @Mutation
    public Uni<Customer> addcustomer(Customer customer){
        return customerRepository.addMutation(customer);
    }

    @Mutation
    public Uni<Boolean> deleteCustomer(Long id) {
        return customerRepository.deleteMutation(id);
    }


}
