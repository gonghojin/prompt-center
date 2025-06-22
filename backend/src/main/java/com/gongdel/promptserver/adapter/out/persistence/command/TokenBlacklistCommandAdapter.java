//package com.gongdel.promptserver.adapter.out.persistence.command;
//
//import com.gongdel.promptserver.adapter.out.persistence.entity.TokenBlacklistEntity;
//import com.gongdel.promptserver.adapter.out.persistence.repository.TokenBlacklistRepository;
//import com.gongdel.promptserver.application.port.out.command.BlacklistTokenPort;
//import com.gongdel.promptserver.domain.user.UserId;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
///**
// * 토큰 블랙리스트 Command 어댑터입니다.
// */
//@Component
//@RequiredArgsConstructor
//@Transactional
//public class TokenBlacklistCommandAdapter implements BlacklistTokenPort {
//    private final TokenBlacklistRepository tokenBlacklistRepository;
//
//    @Override
//    public void blacklistToken(String tokenId, UserId userId, LocalDateTime expiresAt) {
//        TokenBlacklistEntity entity = TokenBlacklistEntity.builder()
//                .tokenId(tokenId)
//                .userId(userId.getValue().toString())
//                .expiresAt(expiresAt)
//                .build();
//
//        tokenBlacklistRepository.save(entity);
//    }
//}
