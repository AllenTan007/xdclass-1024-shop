package net.xdclass.request;

import lombok.Data;

@Data
public class NewUserCouponRequest {

    private Long userId;
    private String name;
}
