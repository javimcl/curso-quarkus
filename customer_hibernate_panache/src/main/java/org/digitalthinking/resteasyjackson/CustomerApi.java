package org.digitalthinking.resteasyjackson;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.digitalthinking.entites.Customer;
import org.digitalthinking.repositories.CustomerRepository;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CustomerApi {
    @Inject
    CustomerRepository repository;


    @GET
    public Uni<List<PanacheEntityBase>> list() {
        return Customer.listAll(Sort.by("names"));
    }


    @GET
    @Path("using-repository")
    public Uni<List<Customer>> listUsingRepository() {
        return repository.findAll().list();
    }

    @GET
    @Path("/{Id}")
    public Uni<PanacheEntityBase> getById(@PathParam("Id") Long Id) {
        return Customer.findById(Id);
    }

    @GET
    @Path("/{Id}/product")
    public Uni<Customer> getByIdProduct(@PathParam("Id") Long Id) {
        return repository.getByIdProduct(Id);
    }

    @POST
    public Uni<Response> add(Customer c) {
        return repository.add(c);
    }

    @DELETE
    @Path("/{Id}")
    public Uni<Response> delete(@PathParam("Id") Long Id) {
        return repository.delete(Id);
    }
    @PUT
    @Path("{id}")
    public Uni<Response> update(@RestPath Long id, Customer c) {
        return repository.update(id,c);
    }



}
