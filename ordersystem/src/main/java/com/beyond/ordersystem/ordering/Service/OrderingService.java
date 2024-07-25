package com.beyond.ordersystem.ordering.Service;


import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import com.beyond.ordersystem.member.Service.MemberService;
import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Repository.OrderingRepository;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Repository.ProductRepository;
import com.beyond.ordersystem.product.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Ordering orderCreate(OrderSaveReqDto dto){
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("member is not found"));
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        for (OrderSaveReqDto.OrderDetailDto orderDetailDto : dto.getOrderDetailDtoList()){
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering )
                    .product(product)
                    .quantity(orderDetailDto.getProductCount())

                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
//        OrderDetail.builder().

        Ordering savedordering = orderingRepository.save(ordering);
        return savedordering;
    }


}