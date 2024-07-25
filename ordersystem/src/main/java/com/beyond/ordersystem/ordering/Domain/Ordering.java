package com.beyond.ordersystem.ordering.Domain;

import com.beyond.ordersystem.member.Domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)

    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();


}