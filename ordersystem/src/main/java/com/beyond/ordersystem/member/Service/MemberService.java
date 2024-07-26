package com.beyond.ordersystem.member.Service;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberLoginDto;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member createMember(MemberSaveReqDto dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        Member member = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        return memberRepository.save(member);
    }


    public Page<MemberResDto> listMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
//        Page<MemberResDto> memberResDtos = members.map(a->a.fromEntity());
        return members.map(a -> a.fromEntity());
    }

    public Member login(MemberLoginDto dto) {
        // email 존재 여부
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("NO EXIST EMAIL"));

        // password 일치 여부
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("PASSWORD do not match.");
        }

        return member;
    }

}



