package net.xdclass.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {

    /**
     * 购物项
     */
    @JsonProperty("cart_items")
    private List<CartItemVO> cartItems;

    /**
     * 购物总件数
     */
    @JsonProperty("total_num")
    private Integer totalNum;

    /**
     * 购物车总价格
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 购物车世纪支付价格
     */
    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;

    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
    }

    public Integer getTotalNum() {
        if (cartItems != null){
            return cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
        }
        return 0;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);
        if(this.cartItems!=null){
            for (CartItemVO cartItem : cartItems) {
                totalAmount.add(cartItem.getTotalAmount());
            }
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRealPayAmount() {
        BigDecimal totalAmount = new BigDecimal(0);
        if(this.cartItems!=null){
            for (CartItemVO cartItem : cartItems) {
                totalAmount.add(cartItem.getTotalAmount());
            }
        }
        return totalAmount;
    }

    public void setRealPayAmount(BigDecimal realPayAmount) {
        this.realPayAmount = realPayAmount;
    }
}
