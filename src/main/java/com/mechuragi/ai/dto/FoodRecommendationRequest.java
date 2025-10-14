package com.mechuragi.ai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class FoodRecommendationRequest {

    @NotNull(message = "추천 타입은 필수입니다")
    private RecommendationType type;

    @Valid
    private Context context;

    @Valid
    private UserPreference userPreference;

    private String userMessage;

    public FoodRecommendationRequest() {}

    public FoodRecommendationRequest(RecommendationType type, Context context, UserPreference userPreference, String userMessage) {
        this.type = type;
        this.context = context;
        this.userPreference = userPreference;
        this.userMessage = userMessage;
    }

    public RecommendationType getType() {
        return type;
    }

    public void setType(RecommendationType type) {
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public UserPreference getUserPreference() {
        return userPreference;
    }

    public void setUserPreference(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public enum RecommendationType {
        WEATHER, TIME_BASED, CONVERSATION
    }

    public static class Context {
        private List<String> weatherConditions;
        private String timeOfDay;

        public Context() {}

        public Context(List<String> weatherConditions, String timeOfDay) {
            this.weatherConditions = weatherConditions;
            this.timeOfDay = timeOfDay;
        }

        public List<String> getWeatherConditions() {
            return weatherConditions;
        }

        public void setWeatherConditions(List<String> weatherConditions) {
            this.weatherConditions = weatherConditions;
        }

        public String getTimeOfDay() {
            return timeOfDay;
        }

        public void setTimeOfDay(String timeOfDay) {
            this.timeOfDay = timeOfDay;
        }
    }

    public static class UserPreference {
        private String dietStatus;
        private String veganOption;
        private String spiceLevel;
        private List<String> foodTypes;
        private List<String> tastes;
        private List<String> dislikedFoods;

        public UserPreference() {}

        public UserPreference(String dietStatus, String veganOption, String spiceLevel,
                            List<String> foodTypes, List<String> tastes, List<String> dislikedFoods) {
            this.dietStatus = dietStatus;
            this.veganOption = veganOption;
            this.spiceLevel = spiceLevel;
            this.foodTypes = foodTypes;
            this.tastes = tastes;
            this.dislikedFoods = dislikedFoods;
        }

        public String getDietStatus() {
            return dietStatus;
        }

        public void setDietStatus(String dietStatus) {
            this.dietStatus = dietStatus;
        }

        public String getVeganOption() {
            return veganOption;
        }

        public void setVeganOption(String veganOption) {
            this.veganOption = veganOption;
        }

        public String getSpiceLevel() {
            return spiceLevel;
        }

        public void setSpiceLevel(String spiceLevel) {
            this.spiceLevel = spiceLevel;
        }

        public List<String> getFoodTypes() {
            return foodTypes;
        }

        public void setFoodTypes(List<String> foodTypes) {
            this.foodTypes = foodTypes;
        }

        public List<String> getTastes() {
            return tastes;
        }

        public void setTastes(List<String> tastes) {
            this.tastes = tastes;
        }

        public List<String> getDislikedFoods() {
            return dislikedFoods;
        }

        public void setDislikedFoods(List<String> dislikedFoods) {
            this.dislikedFoods = dislikedFoods;
        }
    }
}