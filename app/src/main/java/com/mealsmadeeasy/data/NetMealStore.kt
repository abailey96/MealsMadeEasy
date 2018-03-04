package com.mealsmadeeasy.data

import com.mealsmadeeasy.data.service.MealsMadeEasyService
import com.mealsmadeeasy.data.service.model.SparseMealPlan
import com.mealsmadeeasy.model.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import retrofit2.Response
import java.io.IOException

class NetMealStore(
        private val userManager: UserManager,
        private val service: MealsMadeEasyService
) : MealStore {

    private val mealPlan = RxLoader {
        userManager.getUserToken()
                .subscribeOn(Schedulers.io())
                .flatMap { service.getMealPlan(it) }
                .map { it.unwrap() }
    }

    private val suggestedMeals = RxLoader {
        userManager.getUserToken()
                .subscribeOn(Schedulers.io())
                .flatMap { service.getSuggestedMeals(it) }
                .map { it.unwrap() }
    }

    override fun getMealPlan(): Observable<MealPlan> {
        return mealPlan.getOrComputeValue()
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getIngredientsForRecipe(meal: Meal): Observable<List<Ingredient>> {
        TODO("not implemented")
    }

    override fun addMealToMealPlan(meal: Meal, date: DateTime, mealPeriod: MealPeriod, servings: Int) {
        mealPlan.getOrComputeValue().firstOrError()
                .subscribeOn(Schedulers.io())
                .map { it + MealPlanEntry(date.withTimeAtStartOfDay(), mealPeriod, listOf(MealPortion(meal, servings))) }
                .flatMap { updateMealPlan(it)}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun removeMealFromMealPlan(meal: Meal, date: DateTime, mealPeriod: MealPeriod) {
        mealPlan.getOrComputeValue().firstOrError()
                .subscribeOn(Schedulers.io())
                .map { it - MealPlanEntry(date, mealPeriod, listOf(MealPortion(meal, Int.MAX_VALUE))) }
                .flatMap { updateMealPlan(it)}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun updateMealPlan(updatedPlan: MealPlan): Single<Unit> {
        return userManager.getUserToken()
                .flatMap { token -> service.setMealPlan(token, SparseMealPlan(updatedPlan)) }
                .map { it.unwrap() }
                .doOnSuccess { mealPlan.setValue(updatedPlan) }
    }

    override fun getSuggestedMeals(): Single<List<Meal>> {
        return suggestedMeals.getOrComputeValue()
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun findMealById(id: String): Single<Meal> {
        return service.getMeal(id).subscribeOn(Schedulers.io())
                .map { it.unwrap() }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> Response<T>.unwrap(): T {
        if (!isSuccessful) {
            throw IOException("${code()}: ${errorBody()}")
        } else {
            return body() ?: throw IOException("No data was returned")
        }
    }

}