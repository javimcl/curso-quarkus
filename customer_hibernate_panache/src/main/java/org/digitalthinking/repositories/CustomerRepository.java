package org.digitalthinking.repositories;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.digitalthinking.entites.Customer;
import org.digitalthinking.entites.Product;
import org.jboss.resteasy.reactive.RestPath;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Slf4j
@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer,Long> {


    @Inject
    Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8081).setSsl(false).setTrustAll(true));
    }

    public Uni<Customer> getByIdProduct(@PathParam("Id") Long Id) {
        return Uni.combine().all().unis(getCustomerReactive(Id),getAllProducts())
                .combinedWith((v1,v2) -> {
                    v1.getProducts().forEach(product -> {
                        v2.forEach(p -> {
                            log.info("Ids are: " + product.getProduct() +" = " +p.getId());
                            if(product.getProduct() == p.getId()){
                                product.setName(p.getName());
                                product.setDescription(p.getDescription());
                                ;                           }
                        });
                    });
                    return v1;
                });
    }

    private Uni<Customer> getCustomerReactive(Long Id){
        return Customer.findById(Id);
    }

    public Uni<List<Product>> getAllProducts(){
        return webClient.get(8081, "localhost", "/product").send()
                .onFailure().invoke(res -> log.error("Error recuperando productos ", res))
                .onItem().transform(res -> {
                    List<Product> lista = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();
                    objects.forEach(p -> {
                        log.info("See Objects: " + objects);
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Pass JSON string and the POJO class
                        Product product = null;
                        try {
                            product = objectMapper.readValue(p.toString(), Product.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        lista.add(product);
                    });
                    return lista;
                });
    }

    public Uni<Response> delete(Long Id) {
        return Panache.withTransaction(() -> Customer.deleteById(Id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    public Uni<Response> add(Customer c) {
        c.getProducts().forEach(p -> p.setCustomer(c));
        return Panache.withTransaction(c::persist)
                .replaceWith(Response.ok(c).status(CREATED)::build);
    }

    public Uni<Response> update(@RestPath Long id, Customer c) {
        if (c == null || c.getCode() == null) {
            throw new WebApplicationException("Product code was not set on request.", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
        }
        return Panache
                .withTransaction(() -> Customer.<Customer>findById(id)
                        .onItem().ifNotNull().invoke(entity -> {
                            entity.setNames(c.getNames());
                            entity.setAccountNumber(c.getAccountNumber());
                            entity.setCode(c.getCode());
                        })
                )
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    public Uni<Boolean> deleteMutation(Long Id) {
        return Panache.withTransaction(() -> Customer.deleteById(Id));
    }

    public Uni<Customer> addMutation(Customer c) {
        c.getProducts().forEach(p -> p.setCustomer(c));
        return Panache.withTransaction(c::persist)
                .replaceWith(c);
    }

}
