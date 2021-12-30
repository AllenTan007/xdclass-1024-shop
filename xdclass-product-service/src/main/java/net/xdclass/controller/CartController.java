package net.xdclass.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.xdclass.request.CartItemRequest;
import net.xdclass.service.CartService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("购物车")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {



    @Autowired
    private CartService cartService;


    @ApiOperation("添加购物车")
    public JsonData addCart(@RequestBody CartItemRequest cartItemRequest){
        cartService.addToCart(cartItemRequest);
        return JsonData.buildSuccess();
    }



}
