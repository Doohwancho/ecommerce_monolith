package com.ecommerce.monolith.domain.member.controller;

import com.ecommerce.monolith.domain.member.entity.DenormalizedMemberEntity;
import com.ecommerce.monolith.domain.member.service.MemberService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;
    
    @GetMapping
    public ResponseEntity<List<DenormalizedMemberEntity>> getAllUsers() {
        List<DenormalizedMemberEntity> users = memberService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<DenormalizedMemberEntity> getUserByUsername(@PathVariable String userId) {
        DenormalizedMemberEntity user = memberService.getUserByUserId(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
