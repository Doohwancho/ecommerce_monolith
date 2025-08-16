package com.ecommerce.monolith.domain.member.service;

import com.ecommerce.monolith.domain.member.entity.DenormalizedMemberEntity;
import com.ecommerce.monolith.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    public List<DenormalizedMemberEntity> getAllUsers() {
        return memberRepository.findAll();
    }
    
    public DenormalizedMemberEntity getUserByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }
    public boolean userExists(Long memberId) {
        return memberRepository.existsById(memberId);
    }
}