package com.mechuragi.ai.repository;

import com.mechuragi.ai.entity.preference.PreferenceFoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceFoodTypeRepository extends JpaRepository<PreferenceFoodType, Long> {

    List<PreferenceFoodType> findByPreferenceId(Long preferenceId);
}
