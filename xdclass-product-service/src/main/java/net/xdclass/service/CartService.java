package net.xdclass.service;

import net.xdclass.request.CartItemRequest;
import net.xdclass.vo.CartVO;

public interface CartService {

    void addToCart(CartItemRequest cartItemRequest);

    void clear();

    CartVO getMyCart();
}
