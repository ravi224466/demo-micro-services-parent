package com.example.demo.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.InventoryResponse;
import com.example.demo.dto.OrderLineItemsDto;
import com.example.demo.dto.OrderRequest;
import com.example.demo.model.Order;
import com.example.demo.model.OrderLineItems;
import com.example.demo.repository.OrderRepository;

@Service
@Transactional
public class OrderService {
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	WebClient.Builder webClientBuilder;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream().map(this::maptoDto).collect(Collectors.toList());
		order.setOrderLineItems(orderLineItemsList);
		
		List<String> skuCodesList = order.getOrderLineItems().stream().map(orderLineItems -> orderLineItems.getSkuCode()).collect(Collectors.toList());
		
		InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
		.uri("http://INVENTORY-SERVICE/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodesList).build())
		.retrieve()
		.bodyToMono(InventoryResponse[].class)
		.block();
		
		boolean allMatch = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
		
		if(allMatch) {
			orderRepository.save(order);
		}else {
			throw new IllegalArgumentException("Product is not in Stock available ");
		}
		
		
	}

	public OrderLineItems maptoDto(OrderLineItemsDto orderLineItemDto) {
		OrderLineItems items = new OrderLineItems();
		items.setPrice(orderLineItemDto.getPrice());
		items.setQuantity(orderLineItemDto.getQuantity());
		items.setSkuCode(orderLineItemDto.getSkuCode());

		return items;
	}
}
