package com.yieldplanner.service;

import com.yieldplanner.dto.FarmRequest;
import com.yieldplanner.dto.FarmResponse;
import com.yieldplanner.entity.Farm;
import com.yieldplanner.entity.User;
import com.yieldplanner.repository.FarmRepository;
import com.yieldplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public FarmResponse createFarm(Long userId, FarmRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Farm farm = Farm.builder()
                .user(user)
                .name(req.getName())
                .location(req.getLocation())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .region(req.getRegion())
                .soilType(req.getSoilType())
                .build();

        farm = farmRepository.save(farm);
        return toResponse(farm);
    }

    public List<FarmResponse> getUserFarms(Long userId) {
        return farmRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FarmResponse getFarm(Long farmId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));
        return toResponse(farm);
    }

    private FarmResponse toResponse(Farm farm) {
        FarmResponse resp = new FarmResponse();
        resp.setId(farm.getId());
        resp.setName(farm.getName());
        resp.setLocation(farm.getLocation());
        resp.setLatitude(farm.getLatitude());
        resp.setLongitude(farm.getLongitude());
        resp.setRegion(farm.getRegion());
        resp.setSoilType(farm.getSoilType());
        resp.setCreatedAt(farm.getCreatedAt());
        return resp;
    }
}
