package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;
	
	public void createProduct(ProductRequest productRequest) {
		Product product = Product.builder()
		.name(productRequest.getName())
		.description(productRequest.getDescription())
		.price(productRequest.getPrice())
		.build();
		
		productRepository.save(product);
		String productId = product.getId();
		log.info("Product Saved : "+productId);
	}

	public List<ProductResponse> getAllProducts() {
		List<Product> products = productRepository.findAll();
		List<ProductResponse> productResponseList = products.stream().map(this::mapProductResponse).collect(Collectors.toList());
		return productResponseList;
	}
	
	public ProductResponse mapProductResponse(Product product){
		return ProductResponse.builder()
		.id(product.getId())
		.name(product.getName())
		.description(product.getDescription())
		.price(product.getPrice())
		.build();
	}

}
