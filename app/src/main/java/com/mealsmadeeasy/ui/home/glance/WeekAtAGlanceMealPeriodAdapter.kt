package com.mealsmadeeasy.ui.home.glance

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mealsmadeeasy.R
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.model.MealPeriod

class WeekAtAGlanceMealPeriodAdapter(
        meals: Map<MealPeriod, List<Meal>> = emptyMap()
) : RecyclerView.Adapter<WeekAtAGlanceMealPeriodViewHolder>() {

    var meals = meals
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val sortedPeriods: List<MealPeriod>
        get() = meals.keys.sorted()

    override fun getItemCount() = meals.size

    override fun onBindViewHolder(holder: WeekAtAGlanceMealPeriodViewHolder, position: Int) {
        val period = sortedPeriods[position]
        holder.bind(period, meals[period].orEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekAtAGlanceMealPeriodViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(
                R.layout.view_week_at_a_glance_meal_period, parent, false)
        return WeekAtAGlanceMealPeriodViewHolder(root)
    }

}