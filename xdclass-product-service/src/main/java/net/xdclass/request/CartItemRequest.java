package net.xdclass.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CartItemRequest {


    @ApiModelProperty("商品id")
    @JsonProperty("product_id")
    private String productId;

    @ApiModelProperty("购买数量")
    @JsonProperty("buy_num")
    private String buyNum;



}
