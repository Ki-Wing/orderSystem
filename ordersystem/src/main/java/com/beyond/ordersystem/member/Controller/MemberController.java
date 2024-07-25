package com.beyond.ordersystem.member.Controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberSaveReqDto dto) {
        Member member = memberService.createMember(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "member is succuessful created", member.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listMembers(Pageable pageable) {
        Page<MemberResDto> dtos = memberService.listMembers(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "members are found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}




