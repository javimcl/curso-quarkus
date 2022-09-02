package org.digitalthinking.repositories;

import org.digitalthinking.entites.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ProductRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void createdProduct(Product p){
        em.persist(p);
    }

    @Transactional
    public void deleteProduct(Product p){
        em.remove(em.contains(p) ? p : em.merge(p));
    }

    @Transactional
    public List<Product> listProduct(){
        List<Product> products = em.createQuery("select p from Product p").getResultList();
        return products;
    }
    @Transactional
    public Product findProduct(Long Id){
        return em.find(Product.class, Id);
    }
    @Transactional
    public void updateProduct(Product p){
        em.merge(p);
    }
}
