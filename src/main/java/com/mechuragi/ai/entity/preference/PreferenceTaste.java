package com.mechuragi.ai.entity.preference;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "preference_tastes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferenceTaste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id", nullable = false)
    private FoodPreference preference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TasteType tasteType;

    public enum TasteType {
        단맛, 짠맛, 신맛, 쓴맛, 감칠맛, 고소한맛
    }
}
