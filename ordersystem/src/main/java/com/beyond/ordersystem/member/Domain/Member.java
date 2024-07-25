package com.beyond.ordersystem.member.Domain;

import com.beyond.ordersystem.common.domain.Address;
import com.beyond.ordersystem.common.domain.BaseTimeEntity;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;


    public MemberResDto fromEntity() {
        return MemberResDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .address(this.address)
                .build();
    }
}

