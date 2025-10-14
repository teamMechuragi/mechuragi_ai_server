package com.mechuragi.ai.dto;

import java.util.List;

public class FoodRecommendationResponse {

    private String message;
    private List<FoodRecommendation> recommendations;
    private String model;

    public FoodRecommendationResponse() {}

    public FoodRecommendationResponse(String message, List<FoodRecommendation> recommendations, String model) {
        this.message = message;
        this.recommendations = recommendations;
        this.model = model;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FoodRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<FoodRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public static class FoodRecommendation {
        private String name;
        private String description;
        private String reason;
        private String ingredients;
        private String cookingTime;
        private String difficulty;
        private List<String> recipe;

        public FoodRecommendation() {}

        public FoodRecommendation(String name, String description, String reason, String ingredients,
                                String cookingTime, String difficulty, List<String> recipe) {
            this.name = name;
            this.description = description;
            this.reason = reason;
            this.ingredients = ingredients;
            this.cookingTime = cookingTime;
            this.difficulty = difficulty;
            this.recipe = recipe;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }

        public String getCookingTime() {
            return cookingTime;
        }

        public void setCookingTime(String cookingTime) {
            this.cookingTime = cookingTime;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public List<String> getRecipe() {
            return recipe;
        }

        public void setRecipe(List<String> recipe) {
            this.recipe = recipe;
        }
    }
}