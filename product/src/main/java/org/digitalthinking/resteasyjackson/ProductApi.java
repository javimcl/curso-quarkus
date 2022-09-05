package org.digitalthinking.resteasyjackson;


import org.digitalthinking.entites.Product;
import org.digitalthinking.repositories.ProductRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductApi {
 @Inject
 ProductRepository pr;

    @GET
    public List<Product> list() {
        return pr.findAll();
    }

    @GET
    @Path("/{Id}")
    public Product getById(@PathParam("Id") Long Id) {
        return pr.findById(Id).get();
    }

    @POST
    public Response add(Product p) {
        pr.save(p);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{Id}")
    public Response delete(@PathParam("Id") Long Id) {
         pr.delete( getById(Id));
        return Response.ok().build();
    }
    @PUT
    public Response update(Product p) {
        Product product = getById(p.getId());
        product.setCode(p.getCode());
        product.setName(p.getName());
        product.setDescription(p.getDescription());
        pr.save(product);
        return Response.ok().build();
    }


}
