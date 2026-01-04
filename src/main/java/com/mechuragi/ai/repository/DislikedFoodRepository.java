package com.mechuragi.ai.repository;

import com.mechuragi.ai.entity.preference.DislikedFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DislikedFoodRepository extends JpaRepository<DislikedFood, Long> {

    List<DislikedFood> findByPreferenceId(Long preferenceId);
}
