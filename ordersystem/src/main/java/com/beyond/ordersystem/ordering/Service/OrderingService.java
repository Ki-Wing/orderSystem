package com.beyond.ordersystem.ordering.Service;


import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.Repository.OrderingRepository;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Ordering orderCreate(OrderSaveReqDto dto) {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + dto.getMemberId()));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        for (OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()) {
            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + orderDto.getProductId()));

            int quantity = orderDto.getProductCount();
            
            // 변경 감지(더티체킹)으로 인해 별도의 save 불필요
            product.updateStockQuantity(quantity);

            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }

        orderingRepository.save(ordering);
        return ordering;

    }
    public List<OrderListResDto> listorder() {
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();

        for (Ordering order : orderings) {
            orderListResDtos.add(order.fromEntity());
        }
        return orderListResDtos;
    }
}


//    public Ordering orderCreate(OrderSaveReqDto dto){
//        // ordering 생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId())
//                .orElseThrow(()->new EntityNotFoundException("member is not found"));
//        Ordering ordering = Ordering.builder()
//                .member(member)
//                .build();
//
////        {productId, productCount}를 한건한건 받기 위해 for문
//        for (OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()){
//            Product product = productRepository.findById(orderDto.getProductId())
//                    .orElseThrow(()->new EntityNotFoundException("product is not found"));
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .product(product)
//                    .quantity(orderDto.getProductCount())
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);   // 쉬운 방법임. 걍 한번 돌면서
//        }
//
//        return ordering;
//    }
//}