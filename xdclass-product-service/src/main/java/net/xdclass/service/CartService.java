package net.xdclass.service;

import net.xdclass.request.CartItemRequest;

public interface CartService {

    void addToCart(CartItemRequest cartItemRequest);
}
