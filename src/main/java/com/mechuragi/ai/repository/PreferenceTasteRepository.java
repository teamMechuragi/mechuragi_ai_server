package com.mechuragi.ai.repository;

import com.mechuragi.ai.entity.preference.PreferenceTaste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceTasteRepository extends JpaRepository<PreferenceTaste, Long> {

    List<PreferenceTaste> findByPreferenceId(Long preferenceId);
}
