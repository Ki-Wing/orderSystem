package com.beyond.ordersystem.member.Dto;
import com.beyond.ordersystem.common.domain.Address;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveReqDto {
    private String name;

    @NotEmpty(message = "email is essential")
    private String email;

    @NotEmpty(message = "password is essential")
    @Size(min = 8, message = "password-minimum length : 8")
    private String password;

    private Address address;
//    private String city;
//    private String street;
//    private String zipcode;

    private Role role;

    public Member toEntity(String password) {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(password)
                .address(this.address)
//                .address(Address.builder()
//                    .city(this.city).street(this.street).zipcode(this.zipcode)
//                        build())
//                .role(this.role)
                .role(Role.USER)
                .build();
    }
}
