package net.xdclass.service;

import net.xdclass.request.CartItemRequest;
import net.xdclass.vo.CartItemVO;
import net.xdclass.vo.CartVO;

import java.util.List;

public interface CartService {

    void addToCart(CartItemRequest cartItemRequest);

    void clear();

    CartVO getMyCart();

    void deleteItem(long productId);

    void changeItemNum(CartItemRequest cartItemRequest);

    List<CartItemVO> confirmOrderCartItem(List<Long> productIdList);
}
