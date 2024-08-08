package com.example.healthylife.controller;

import com.example.healthylife.config.jwt.JwtUtil;
import com.example.healthylife.entity.UserEntity;
import com.example.healthylife.service.JwtAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/jwt")
@RestController
@ApiOperation("로그인 컨트롤러")
public class JwtLoginAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtAuthService jwtAuthService;
    private final ObjectMapper objectMapper;

    @ApiOperation("로그인 컨트롤러")
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password){
        try {
            //사용자 이름(아이디)과 비번으로 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            //인증 성공하면 JWT 토큰 생성
            //accessToken 생성
            String accessToken = jwtUtil.createAccessToken(UserEntity.builder()
                    .userId(authentication.getName())
                    .build());
            //refreshToken 생성
            String refreshToken = jwtUtil.createRefreshToken(UserEntity.builder()
                    .userId(authentication.getName())
                    .build());
            jwtAuthService.addRefreshToken(refreshToken, username);

            Map result = Map.of("access-token", accessToken,
                    "refresh-token", refreshToken);

            //생성된 토큰을 ResponseEntity로 반환
            return ResponseEntity.ok()
                    .body(objectMapper.writeValueAsString(result));
        } catch (UsernameNotFoundException | BadCredentialsException exception){
            //사용자 이름이나 비번이 다른 경우 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");

            //todo 아이디 or 비번 따로따로 바꾸고 싶으면 catch 하나 더 주기
        } catch (Exception e) {
            log.error("authenticate failed! - username: {}, password: {}", username, password, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception : " + e.getMessage());
        }


    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam("refresh-token") String refreshToken){
        try {
            String accessToken = jwtAuthService.refresh(refreshToken);
            //사용자 이름(아이디)과 비번으로 인증


            Map result = Map.of("access-token", accessToken,
                    "refresh-token", refreshToken);

            //생성된 토큰을 ResponseEntity로 반환
            return ResponseEntity.ok()
                    .body(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("refresh failed! - refresh-token: {}", refreshToken, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception : " + e.getMessage());
        }

    }


    @ApiOperation("로그아웃 컨트롤러")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("username") String username) {
        // 클라이언트 측에서 토큰을 삭제하도록 처리
//        response.setHeader("Set-Cookie", "accessToken=; HttpOnly; Path=/; Max-Age=0");
//        response.setHeader("Set-Cookie", "refreshToken=; HttpOnly; Path=/; Max-Age=0");
        jwtAuthService.logout(username);

        return ResponseEntity.ok().body("Logout Successful");
    }


}