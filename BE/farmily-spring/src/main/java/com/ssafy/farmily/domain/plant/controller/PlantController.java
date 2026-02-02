package com.ssafy.farmily.domain.plant.controller;

import com.ssafy.farmily.domain.plant.dto.PlantRegistRequest;
import com.ssafy.farmily.domain.plant.dto.PlantResponse;
import com.ssafy.farmily.domain.plant.dto.RefPlantSpeciesResponse;
import com.ssafy.farmily.domain.plant.service.PlantService;
import com.ssafy.farmily.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plant")
public class PlantController {

    private final PlantService plantService;
    private final AuthUtil authUtil;

    /**
     * 등록 가능한 식물 종 목록 조회
     */
    @GetMapping("/species")
    public List<RefPlantSpeciesResponse> getSpeciesList() {
        return plantService.getSpeciesList();
    }

    /**
     * 내 식물 목록 조회
     */
    @GetMapping
    public List<PlantResponse> getMyPlants() {
        String email = authUtil.getCurrentEmail();
        return plantService.getPlants(email);
    }

    /**
     * 식물 등록
     */
    @PostMapping
    public Map<String, Long> registPlant(@RequestBody PlantRegistRequest request) {
        String email = authUtil.getCurrentEmail();
        Long plantId = plantService.registPlant(email, request);
        return Map.of("plantId", plantId);
    }
}
