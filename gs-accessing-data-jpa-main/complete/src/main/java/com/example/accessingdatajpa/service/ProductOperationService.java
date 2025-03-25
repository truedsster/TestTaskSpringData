package com.example.accessingdatajpa.service;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductOperationService {
    @Autowired
    ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Product product) {
        return getProductById(product.getId()).isPresent()? Optional.of(productRepository.save(product)) : Optional.empty();
    }

    public Optional<Product> getProductById(long id) {
        return Optional.ofNullable(productRepository.findById(id));
    }

    public boolean deleteProductById(long id) {
        if (getProductById(id).isPresent()) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Product> getProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getByNameAndSortProducts(String name, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, "price");

        return productRepository.findByNameContainingIgnoreCase(name, sort);
    }

}