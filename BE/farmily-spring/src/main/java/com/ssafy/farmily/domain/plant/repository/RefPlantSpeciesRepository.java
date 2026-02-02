package com.ssafy.farmily.domain.plant.repository;

import com.ssafy.farmily.domain.plant.entity.RefPlantSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefPlantSpeciesRepository extends JpaRepository<RefPlantSpecies, Long> {
}
