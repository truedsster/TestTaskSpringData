package com.example.accessingdatajpa;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import com.example.accessingdatajpa.service.ProductOperationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductOperationService productService;

    @Test
    public void saveProduct_shouldReturnSavedProduct() {
        Product product = new Product("Product1", "Product desc", new BigDecimal("1"));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product saved = productService.saveProduct(product);

        assertNotNull(saved);
        assertEquals("Product1", saved.getName());
        verify(productRepository).save(product);
    }

    @Test
    public void getProductById_shouldReturnProduct() {
        Product product = new Product("Product2", "Product desc", new BigDecimal("2"));
        when(productRepository.findById(1L)).thenReturn(product);

        Optional<Product> found = productService.getProductById(1L);

        assertTrue(found.isPresent());
        assertEquals("Product2", found.get().getName());
    }

    @Test
    public void searchProducts_shouldReturnSortedResults() {
        List<Product> products = List.of(
                new Product("Product3", "Product desc", new BigDecimal("3")),
                new Product("Product4", "Product desc", new BigDecimal("4"))
        );
        when(productRepository.findByNameContainingIgnoreCase("product", Sort.by(Sort.Direction.ASC, "price")))
                .thenReturn(products);

        List<Product> result = productService.getByNameAndSortProducts("product", "ASC");

        assertEquals(2, result.size());
        assertEquals("Product3", result.get(0).getName());
    }

    @Test
    public void getProductById_shouldReturnEmptyForNonExistingId() {
        when(productRepository.findById(99L)).thenReturn(null);

        Optional<Product> found = productService.getProductById(99L);

        assertFalse(found.isPresent());
    }

    @Test
    public void deleteProduct_shouldReturnFalseForNonExistingId() {
        when(productRepository.findById(99L)).thenReturn(null);

        boolean result = productService.deleteProductById(99L);

        assertFalse(result);
        verify(productRepository).findById(99L);
    }
}
