package com.beyond.ordersystem.ordering.Controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Service.OrderingService;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/order")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody OrderSaveReqDto dto) {
        Ordering ordering = orderingService.orderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "정상완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

    @GetMapping("/list")
    public ResponseEntity<?> listorder() {
        List<OrderListResDto> ordering = orderingService.listorder();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상조회완료", ordering);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}

