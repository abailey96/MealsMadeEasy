package com.mealsmadeeasy.data.service

import com.mealsmadeeasy.data.service.model.SparseMealPlan
import com.mealsmadeeasy.model.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

fun MealsMadeEasyService.getSearchResults(query: String, filters: List<Filter>)
        = getSearchResults(query, filters.joinToString(separator = ",") { it.id })

interface MealsMadeEasyService {

    @GET("user/profile")
    fun getUserProfile(
            @Header("Authorization") token: String
    ): Single<Response<UserProfile?>>

    @POST("user/profile")
    fun setUserProfile(
            @Header("Authorization") token: String,
            @Body profile: UserProfile
    ): Single<Response<Unit>>

    @GET("/user/plan")
    fun getMealPlan(
            @Header("Authorization") token: String
    ): Single<Response<MealPlan>>

    @POST("/user/plan")
    fun setMealPlan(
            @Header("Authorization") token: String,
            @Body mealPlan: SparseMealPlan
    ): Single<Response<Unit>>

    @GET("/user/groceries")
    fun getGroceryList(
            @Header("Authorization") token: String
    ): Single<Response<GroceryList>>

    @HTTP(path = "/user/groceries/purchased", method = "PUT", hasBody = true)
    fun markIngredientPurchased(
            @Header("Authorization") token: String,
            @Body ingredient: Ingredient
    ): Single<Response<Unit>>

    @HTTP(path = "/user/groceries/purchased", method = "DELETE", hasBody = true)
    fun markIngredientNotPurchased(
            @Header("Authorization") token: String,
            @Body ingredient: Ingredient
    ): Single<Response<Unit>>

    @GET("/meals/suggested")
    fun getSuggestedMeals(
            @Header("Authorization") token: String
    ): Single<Response<List<Meal>>>

    @GET("/meal/{id}")
    fun getMeal(
            @Path("id") mealId: String
    ): Single<Response<Meal>>

    @GET("/recipe/{id}")
    fun getRecipe(
            @Path("id") mealId: String
    ): Single<Response<Recipe>>

    @GET("/meals/search")
    fun getSearchResults(
            @Query("q") query: String,
            @Query("filters") filters: String
    ): Single<Response<List<Meal>>>

    @GET("/meals/search/filters")
    fun getAvailableFilters(): Single<Response<List<FilterGroup>>>

}