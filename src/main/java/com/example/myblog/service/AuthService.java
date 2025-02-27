package com.example.myblog.service;

import com.example.myblog.dto.LoginRequest;
import com.example.myblog.dto.SignupRequest;
import com.example.myblog.dto.TokenResponse;
import com.example.myblog.entity.User;
import com.example.myblog.config.JwtUtil;
import com.example.myblog.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * ✅ 생성자 주입
     * - Spring이 의존성을 자동으로 주입하도록 설정
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * ✅ 회원가입 메서드
     * - username이 이미 존재하는지 확인
     * - 비밀번호를 암호화하여 저장
     * - 기본 역할(ROLE_USER) 추가
     */
    public String signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        // 🔥 새로운 사용자 객체 생성
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // ✅ 비밀번호 암호화 저장
        user.setEmail(signupRequest.getEmail());

        // 🔥 역할(ROLE) 설정
        Set<String> roles = signupRequest.getRoles() != null ? new HashSet<>(signupRequest.getRoles()) : new HashSet<>();
        if (roles.isEmpty()) {
            roles.add("ROLE_USER"); // 기본 역할 부여
        }
        user.setRoles(roles);

        userRepository.save(user);
        return "회원가입 성공!";
    }

    /**
     * ✅ 로그인 메서드
     * - username 기반으로 사용자 조회
     * - 비밀번호 검증
     * - Access Token 및 Refresh Token 생성 후 반환
     */
    public TokenResponse login(LoginRequest loginRequest) {
        // 🔍 사용자 찾기
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 🔍 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 🔥 JWT 토큰 생성 (Access & Refresh)
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // 🔥 Redis에 Refresh Token 저장 (기존 토큰 삭제 후 저장)
        String redisKey = "refresh_token:" + user.getUsername();
        redisTemplate.delete(redisKey);  // 기존 값 삭제
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * ✅ 리프레시 토큰을 이용한 새로운 액세스 토큰 발급
     * - Redis에 저장된 Refresh Token과 비교하여 유효성 검증
     * - Refresh Token이 일치하면 새로운 Access Token 발급
     */
    public String refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        String redisKey = "refresh_token:" + username;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않거나 만료되었습니다.");
        }

        if (!storedToken.equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다.");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return jwtUtil.generateAccessToken(user.getUsername(), user.getRoles());
    }

    /**
     * ✅ 로그아웃 메서드 (Redis에서 Refresh Token 삭제)
     * - 사용자를 찾아 Redis에서 해당 Refresh Token을 제거
     */
    public void logout(String identifier) {
        logger.info("🔍 로그아웃 요청 - identifier: {}", identifier);

        // 🔍 사용자가 username인지 email인지 확인 후 조회
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("❌ 로그아웃 실패 - 사용자를 찾을 수 없습니다.")));

        logger.info("✅ 로그아웃 성공 - username: {}", user.getUsername());

        // 🔥 Redis Key 생성 (일반 로그인: username, 소셜 로그인: email)
        String redisKey = user.getOauthProvider() != null ? "refresh_token:" + user.getEmail() : "refresh_token:" + user.getUsername();

        // 🔥 Redis에서 Refresh Token 삭제
        Boolean deleted = redisTemplate.delete(redisKey);

        if (Boolean.TRUE.equals(deleted)) {
            logger.info("✅ 로그아웃 성공 - Refresh Token 삭제됨: {}", redisKey);
        } else {
            logger.warn("⚠️ 로그아웃 실패 - Redis에서 삭제되지 않음: {}", redisKey);
        }
    }
}
