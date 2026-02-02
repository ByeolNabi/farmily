package com.ssafy.farmily.domain.plant.service;

import com.ssafy.farmily.domain.plant.dto.PlantRegistRequest;
import com.ssafy.farmily.domain.plant.dto.PlantResponse;
import com.ssafy.farmily.domain.plant.dto.RefPlantSpeciesResponse;
import com.ssafy.farmily.domain.plant.entity.Plant;
import com.ssafy.farmily.domain.plant.entity.RefPlantSpecies;
import com.ssafy.farmily.domain.plant.repository.PlantRepository;
import com.ssafy.farmily.domain.plant.repository.RefPlantSpeciesRepository;
import com.ssafy.farmily.domain.user.entity.User;
import com.ssafy.farmily.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlantService {

    private final PlantRepository plantRepository;
    private final RefPlantSpeciesRepository refPlantSpeciesRepository;
    private final UserRepository userRepository;

    /**
     * 모든 식물 종 조회
     */
    public List<RefPlantSpeciesResponse> getSpeciesList() {
        return refPlantSpeciesRepository.findAll().stream()
                .map(RefPlantSpeciesResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 식물 목록 조회
     */
    public List<PlantResponse> getPlants(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return plantRepository.findByUserId(user.getId()).stream()
                .map(plant -> {
                    RefPlantSpecies species = refPlantSpeciesRepository.findById(plant.getRefPlantSpeciesId())
                            .orElseThrow(() -> new IllegalArgumentException("식물 종 정보를 찾을 수 없습니다."));
                    return PlantResponse.from(plant, RefPlantSpeciesResponse.from(species));
                })
                .collect(Collectors.toList());
    }

    /**
     * 식물 등록
     */
    @Transactional
    public Long registPlant(String email, PlantRegistRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RefPlantSpecies species = refPlantSpeciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식물 종입니다."));

        Plant plant = Plant.builder()
                .userId(user.getId())
                .refPlantSpeciesId(species.getId())
                .nickname(request.getNickname())
                .profileImageUrl(species.getImageUrl()) // 기본 이미지는 종 이미지로 설정
                .startedAt(LocalDateTime.now())
                .build();
        
        // isActive는 @PrePersist로 자동 true 설정됨
        
        Plant savedPlant = plantRepository.save(plant);
        return savedPlant.getId();
    }
}
