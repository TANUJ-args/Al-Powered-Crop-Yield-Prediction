package com.yieldplanner.controller;

import com.yieldplanner.dto.FarmRequest;
import com.yieldplanner.dto.FarmResponse;
import com.yieldplanner.security.JwtUtil;
import com.yieldplanner.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<FarmResponse> createFarm(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody FarmRequest request) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(farmService.createFarm(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> getUserFarms(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(farmService.getUserFarms(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponse> getFarm(@PathVariable Long id) {
        return ResponseEntity.ok(farmService.getFarm(id));
    }

    private Long getUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }
}
