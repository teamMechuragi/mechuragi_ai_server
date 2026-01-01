package com.mechuragi.ai.repository;

import com.mechuragi.ai.entity.preference.FoodPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodPreferenceRepository extends JpaRepository<FoodPreference, Long> {

    @Query("SELECT fp FROM FoodPreference fp WHERE fp.member.id = :memberId AND fp.isActive = true")
    Optional<FoodPreference> findActiveByMemberId(@Param("memberId") Long memberId);
}
