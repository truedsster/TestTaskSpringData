package com.example.accessingdatajpa.controller;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.service.ProductOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    ProductOperationService productOperationService;

    // Создание нового продукта
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productOperationService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Получение продукта по ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> productData = productOperationService.getProductById(id);

        return productData.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Обновление продукта
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        var productRes = productOperationService.updateProduct(product);
            return productRes.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Удаление продукта
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        var resp = productOperationService.deleteProductById(id);
        return resp ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Демонстрационный метод для CRUD операций
    @GetMapping("/demo-crud")
    public ResponseEntity<String> demonstrateCrudOperations() {
        StringBuilder result = new StringBuilder();
        result.append("CRUD Демо:\n\n");

        // 1. Create - Создание продукта
        result.append("1. Создание нового product");
        Product newProduct = new Product("Demo Product", "CRUD демо", new BigDecimal("99.99"));
        Product savedProduct = productOperationService.saveProduct(newProduct);
        result.append("   Создан: ").append(savedProduct).append("\n\n");

        // 2. Read - Чтение продукта
        result.append("2. Чтение созданного product \n");
        Product foundProduct = productOperationService.getProductById(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        result.append("   Найден: ").append(foundProduct).append("\n\n");

        // 3. Update - Обновление продукта
        result.append("3. Обновление product \n");
        foundProduct.setPrice(new BigDecimal("129.99"));
        Product updatedProduct = productOperationService.saveProduct(foundProduct);
        result.append("   Обновлен: ").append(updatedProduct).append("\n\n");

        // 4. Delete - Удаление продукта
        result.append("4. Удаление product\n");
        productOperationService.deleteProductById(updatedProduct.getId());
        result.append("   Удален product с ID: ").append(updatedProduct.getId()).append("\n\n");

        // 5. Verify - Проверка удаления
        result.append("5. Проверка удаления \n");
        Optional<Product> deletedProduct = productOperationService.getProductById(updatedProduct.getId());
        result.append("   Product существует после удаления? ").append(deletedProduct.isPresent());

        return ResponseEntity.ok(result.toString());
    }

    // Демонстрационный метод для поиска с сортировкой
    @GetMapping("/demo-search")
    public ResponseEntity<String> demoSearchWithSorting() {

        productOperationService.saveProduct(new Product("Product1 one", "Description 1", new BigDecimal("1")));
        productOperationService.saveProduct(new Product("Product2 one", "Description 2", new BigDecimal("2")));
        productOperationService.saveProduct(new Product("Product3 two", "Description 3", new BigDecimal("3")));
        productOperationService.saveProduct(new Product("Product4 two", "Description 4", new BigDecimal("4")));
        productOperationService.saveProduct(new Product("Product5 three", "Description 5", new BigDecimal("5")));
        productOperationService.saveProduct(new Product("Product6 three", "Description 6", new BigDecimal("6")));
        productOperationService.saveProduct(new Product("Product7 three", "Description 7", new BigDecimal("7")));

        StringBuilder sb = new StringBuilder();
        sb.append("Демо поиска продуктов с сортировкой:\n\n");

        sb.append("1. Продукты, содержащие 'one' (unsorted):\n");
        productOperationService.getProductByName("one")
                .forEach(p -> sb.append("- ").append(p.getName()).append(": ").append(p.getPrice()).append("\n"));

        sb.append("\n2. Продукты, содержащие 'two' отсортированные по цене ASC:\n");
        productOperationService.getByNameAndSortProducts("two", "ASC")
                .forEach(p -> sb.append("- ").append(p.getName()).append(": ").append(p.getPrice()).append("\n"));

        sb.append("\n3. Продукты, содержащие 'three' отсортированные по цене DESC:\n");
        productOperationService.getByNameAndSortProducts("three", "DESC")
                .forEach(p -> sb.append("- ").append(p.getName()).append(": ").append(p.getPrice()).append("\n"));

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }
}