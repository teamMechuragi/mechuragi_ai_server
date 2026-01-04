package com.mechuragi.ai.entity.preference;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String preferenceName;

    @Column(nullable = false)
    private Boolean isActive = false;

    @Column(nullable = false)
    private Integer numberOfDiners = 1;

    @Column(columnDefinition = "TEXT")
    private String allergyInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietStatus isOnDiet = DietStatus.해당_없음;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VeganOption veganOption = VeganOption.해당없음;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpiceLevel spiceLevel = SpiceLevel.순한맛;

    public enum DietStatus {
        다이어트_중, 해당_없음
    }

    public enum VeganOption {
        락토_베지테리언, 락토_오보_베지테리언, 비건, 오보_베지테리언,
        페스코_베지테리언, 폴로_베지테리언, 프루테리언, 플렉시테리언, 해당없음
    }

    public enum SpiceLevel {
        맵찔이, 순한맛, 신라면, 불닭, 핵불닭
    }
}
