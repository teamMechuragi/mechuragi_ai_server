package com.mechuragi.ai.service;

import com.mechuragi.ai.dto.FoodPreferenceDto;
import com.mechuragi.ai.entity.preference.FoodPreference;
import com.mechuragi.ai.repository.DislikedFoodRepository;
import com.mechuragi.ai.repository.FoodPreferenceRepository;
import com.mechuragi.ai.repository.PreferenceFoodTypeRepository;
import com.mechuragi.ai.repository.PreferenceTasteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FoodPreferenceService {

    private final FoodPreferenceRepository foodPreferenceRepository;
    private final PreferenceFoodTypeRepository preferenceFoodTypeRepository;
    private final PreferenceTasteRepository preferenceTasteRepository;
    private final DislikedFoodRepository dislikedFoodRepository;

    public FoodPreferenceDto getActivePreference(Long memberId) {
        FoodPreference preference = foodPreferenceRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("활성화된 음식 선호도를 찾을 수 없습니다."));

        List<String> foodTypes = preferenceFoodTypeRepository.findByPreferenceId(preference.getId())
                .stream()
                .map(ft -> ft.getFoodType().name())
                .collect(Collectors.toList());

        List<String> tastes = preferenceTasteRepository.findByPreferenceId(preference.getId())
                .stream()
                .map(t -> t.getTasteType().name())
                .collect(Collectors.toList());

        List<String> dislikedFoods = dislikedFoodRepository.findByPreferenceId(preference.getId())
                .stream()
                .map(df -> df.getFoodName())
                .collect(Collectors.toList());

        log.info("사용자 선호도 조회 완료 - 회원: {}, 선호도명: {}", memberId, preference.getPreferenceName());

        return FoodPreferenceDto.builder()
                .dietStatus(preference.getIsOnDiet().name())
                .veganOption(preference.getVeganOption().name())
                .spiceLevel(preference.getSpiceLevel().name())
                .foodTypes(foodTypes)
                .tastes(tastes)
                .dislikedFoods(dislikedFoods)
                .build();
    }
}
