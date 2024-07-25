package com.beyond.ordersystem.ordering.Dto;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.OrderStatus;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaveReqDto {
    private Long memberId;
    private List<OrderDetailDto> orderDetailDtoList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        private Long productId;
        private Integer productCount;

    }


}
