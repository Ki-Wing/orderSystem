package com.beyond.ordersystem.member.Controller;

import com.beyond.ordersystem.common.auth.jwtTokenprovider;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberLoginDto;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController

@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final jwtTokenprovider jwtTokenprovider;

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

//    @Autowired
//    public MemberController(
//            MemberService memberService, jwtTokenprovider jwtTokenprovider
//    ){
//        this.memberService = memberService;
//        this.jwtTokenprovider = jwtTokenprovider;
//    }

    @PostMapping("/member/create")
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberSaveReqDto dto) {
        Member member = memberService.createMember(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "member is succuessful created", member.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("member/list")
    public ResponseEntity<?> listMembers(Pageable pageable) {
        Page<MemberResDto> dtos = memberService.listMembers(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "members are found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


        //일치할 경우 acessToken 생성


        @PostMapping("/doLogin")
        public ResponseEntity<?> memberLogin(@Valid @RequestBody MemberLoginDto memberLoginDto){
            //email, password가 일치한지 검증

            Member member = memberService.login(memberLoginDto);
            // 일치할 경우 토큰 생성
            String jwtToken = jwtTokenprovider.createToken(member.getEmail(), member.getRole().toString());

            //생성된 토큰을 CommonResDto에 담아 사용자에게 return
            Map<String, Object> logininfo = new HashMap<>();
            logininfo.put("id",member.getId());
            logininfo.put("token", jwtToken);

            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "[sucessed] LOGIN]", logininfo);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        }


}
