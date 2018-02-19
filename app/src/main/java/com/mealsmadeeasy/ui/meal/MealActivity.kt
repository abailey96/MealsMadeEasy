package com.mealsmadeeasy.ui.meal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mealsmadeeasy.MealsApplication
import com.mealsmadeeasy.R
import com.mealsmadeeasy.data.MealStore
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.ui.BaseActivity
import com.squareup.picasso.Picasso
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import javax.inject.Inject

class MealActivity : BaseActivity() {

    @Inject lateinit var mealStore: MealStore

    companion object {
        private const val TAG = "AddToPlanActivity"
        fun newIntent(context: Context?, id: String) = Intent(context, MealActivity::class.java)
                .putExtra("meal_id", id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MealsApplication.component(this).inject(this)

        setContentView(R.layout.activity_meal)

        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.meal_page_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mealStore.findMealById(intent.getStringExtra("meal_id"))
                .bindToLifecycle(this)
                .subscribe({meal ->
                    finishSetup(meal)
                }, { throwable ->
                    Log.e(TAG, "Failed to load suggestions", throwable)
                    Toast.makeText(this, R.string.meal_page_failed_to_load_meal, Toast.LENGTH_SHORT).show()
                })
    }

    private fun finishSetup(meal: Meal) {
        supportActionBar?.title = meal.name

        val thumbnailView = findViewById<ImageView>(R.id.meal_thumbnail)
        val nameView = findViewById<TextView>(R.id.meal_name)
        val descriptionView = findViewById<TextView>(R.id.meal_description)
        Picasso.with(this)
                .load(meal.thumbnailUrl)
                .fit()
                .centerCrop()
                .into(thumbnailView)
        nameView.text = meal.name
        descriptionView.text = meal.description

        val addButton = findViewById<FloatingActionButton>(R.id.meal_add_to_plan)
        addButton.setOnClickListener {
            startActivity(AddToPlanActivity.newIntent(this, meal.id))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}