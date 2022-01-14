package net.xdclass.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.constant.CacheKey;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.model.LoginUser;
import net.xdclass.request.CartItemRequest;
import net.xdclass.service.CartService;
import net.xdclass.service.ProductService;
import net.xdclass.vo.CartItemVO;
import net.xdclass.vo.CartVO;
import net.xdclass.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void addToCart(CartItemRequest cartItemRequest) {

        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        //获取购物车
        BoundHashOperations<String,Object,Object> myCart =  getMyCartOps();

        Object cacheObj = myCart.get(productId);
        String result = "";

        if(cacheObj!=null){
            result =  (String)cacheObj;
        }

        if(StringUtils.isBlank(result)){
            //不存在则新建一个商品
            CartItemVO cartItemVO = new CartItemVO();

            ProductVO productVO = productService.findDetailById(productId);
            if(productVO == null){throw new BizException(BizCodeEnum.CART_FAIL);}

            cartItemVO.setAmount(productVO.getAmount());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());
            myCart.put(productId,JSON.toJSONString(cartItemVO));

        }else {
            //存在商品，修改数量
            CartItemVO cartItem = JSON.parseObject(result,CartItemVO.class);
            cartItem.setBuyNum(cartItem.getBuyNum()+buyNum);
            myCart.put(productId,JSON.toJSONString(cartItem));
        }

    }

    @Override
    public void clear() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
    }

    @Override
    public CartVO getMyCart() {

        List<CartItemVO> cartItemVOList = buildCartItem(false);
        //封装成cartvo
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);
        return cartVO;

    }

    @Override
    public void deleteItem(long productId) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        myCartOps.delete(productId);
    }

    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        Object item = myCartOps.get(cartItemRequest.getProductId());
        if (item == null){
            throw new BizException(BizCodeEnum.CART_FAIL);
        }
        CartItemVO cartItemVO =  JSON.parseObject((String) item,CartItemVO.class);
        cartItemVO.setBuyNum(cartItemRequest.getBuyNum());
        myCartOps.put(cartItemRequest.getProductId(),JSON.toJSONString(cartItemVO));
    }

    private List<CartItemVO> buildCartItem(boolean latestPrice) {

        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        List<Object> itemList = myCartOps.values();
        List<CartItemVO> cartItemVOList = new ArrayList<>();


        //拼接id列表查询最新价格
        List<Long> productIdList = new ArrayList<>();
        for (Object item : itemList) {
            CartItemVO cartItemVO = JSON.parseObject((String) item, CartItemVO.class);
            cartItemVOList.add(cartItemVO);
            productIdList.add(cartItemVO.getProductId());
        }

        //查询最新的商品价格
        if (latestPrice){
            setProductLatestPrice(cartItemVOList,productIdList);
        }
        return cartItemVOList;
    }

    /**
     * 设置商品最新价格
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatestPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {

        //批量查询
        List<ProductVO> productVOList = productService.findProductsByIdBatch(productIdList);

        Map<Long, ProductVO> maps = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        cartItemVOList.stream().forEach(item->{
            ProductVO productVO = maps.get(item.getProductId());
            item.setAmount(productVO.getAmount());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
        });
    }


    /**
     * 抽取我的购物车，通用方法
     * @return
     */
    private BoundHashOperations<String,Object,Object> getMyCartOps(){
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }


    /**
     * 购物车 key
     * @return
     */
    private String getCartKey(){
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String cartKey = String.format(CacheKey.CART_KEY,loginUser.getId());
        return cartKey;

    }


}
