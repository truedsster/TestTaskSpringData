package com.example.accessingdatajpa;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    public void whenSaveProduct_thenProductIsPersisted() {
        Product product = new Product("Test Product", "Test Description", new BigDecimal("1"));
        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals(product.getName(), saved.getName());
        assertEquals(product.getDescription(), saved.getDescription());
        assertEquals(product.getPrice(), saved.getPrice());
    }

    @Test
    public void whenFindByNameContaining_thenReturnProducts() {
        productRepository.save(new Product("Product1", "Product desc", new BigDecimal("1")));
        productRepository.save(new Product("Product one", "Product desc", new BigDecimal("2")));
        productRepository.save(new Product("Product one", "Product desc", new BigDecimal("3")));

        List<Product> found = productRepository.findByNameContainingIgnoreCase("one");

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(p -> p.getName().toLowerCase().contains("one")));
    }

    @Test
    public void whenFindByNameContainingWithSort_thenReturnSortedProducts() {
        productRepository.save(new Product("Product1", "Product desc", new BigDecimal("1")));
        productRepository.save(new Product("Product one", "Product desc", new BigDecimal("2")));
        productRepository.save(new Product("Product one", "Product desc", new BigDecimal("3")));

        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "one",
                Sort.by(Sort.Direction.ASC, "price")
        );

        assertEquals(2, found.size());
        assertEquals("Product one", found.get(0).getName());
        assertEquals("Product one", found.get(1).getName());
    }

    @Test
    public void whenFindByNonExistingName_thenReturnEmptyList() {
        productRepository.save(new Product("Test Product", "Desc", new BigDecimal("10.00")));

        List<Product> found = productRepository.findByNameContainingIgnoreCase("nonexisting");

        assertTrue(found.isEmpty());
    }
}
