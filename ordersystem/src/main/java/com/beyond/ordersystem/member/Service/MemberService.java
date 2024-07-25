package com.beyond.ordersystem.member.Service;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member createMember(MemberSaveReqDto dto) {
        Member member = dto.toEntity();
        return memberRepository.save(member);
    }

    public Page<MemberResDto> listMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
//        Page<MemberResDto> memberResDtos = members.map(a->a.fromEntity());
        return members.map(a->a.fromEntity());
    }
}




