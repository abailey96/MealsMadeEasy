package com.mealsmadeeasy.ui.home.glance

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mealsmadeeasy.R
import com.mealsmadeeasy.model.MealPortion
import com.squareup.picasso.Picasso

class WeekAtAGlanceMealViewHolder(
        root: View,
        private val onDeleteMeal: (MealPortion) -> Unit,
        private val onUpdateMeal: (MealPortion) -> Unit
) : RecyclerView.ViewHolder(root) {

    private lateinit var mealPortion : MealPortion
    private val mealName = root.findViewById<TextView>(R.id.week_at_a_glance_meal_name)
    private val mealImage = root.findViewById<ImageView>(R.id.week_at_a_glance_meal_background)

    companion object {
        private const val TAG = "WeekAtAGlanceMealViewHolder"
    }

    init {
        val menu = root.findViewById<ImageView>(R.id.week_at_a_glance_menu)
        menu.setOnClickListener { onClickPopupMenu(menu) }
    }

    fun bind(meal: MealPortion) {
        mealPortion = meal
        mealName.text = mealPortion.meal.name
        if (mealPortion.meal.thumbnailUrl != null) {
            mealImage.visibility = View.VISIBLE
            Picasso.with(itemView.context)
                    .load(mealPortion.meal.thumbnailUrl)
                    .into(mealImage)
        } else {
            mealImage.visibility = View.GONE
        }
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_item_delete_meal -> {
                onDeleteMeal(mealPortion)
                return true
            }
            R.id.menu_item_edit_servings -> {
                val res = itemView.context.resources
                val fragment = EditServingsFragment.newInstance(
                        res.getString(R.string.week_at_a_glance_edit_servings_title, mealName.text),
                        mealPortion.servings)
                fragment.show((itemView.context as FragmentActivity).supportFragmentManager, TAG)
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun onClickPopupMenu(menu : ImageView) {
        val popup = PopupMenu(itemView.context!!, menu, Gravity.END)
        popup.menuInflater.inflate(R.menu.instance_meal, popup.menu)
        popup.setOnMenuItemClickListener({ menuItem ->
            onMenuItemClick(menuItem)
        })
        popup.show()
    }
}

private const val KEY_SAVED_DIALOG_TITLE = "EditServingsFragment.TITLE"
private const val KEY_SAVED_SERVINGS = "EditServingsFragment.SERVINGS"

class EditServingsFragment : DialogFragment() {
    private var numServings: Int = -1

    companion object {
        fun newInstance(title: String, numServings: Int): EditServingsFragment {
            val frag = EditServingsFragment()
            val args = Bundle()
            args.putString(KEY_SAVED_DIALOG_TITLE, title)
            args.putInt(KEY_SAVED_SERVINGS, numServings)
            frag.arguments = args

            return frag
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString(KEY_SAVED_DIALOG_TITLE, arguments?.getString(KEY_SAVED_DIALOG_TITLE))
        savedInstanceState.putInt(KEY_SAVED_SERVINGS, numServings)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(KEY_SAVED_DIALOG_TITLE)
        numServings = savedInstanceState?.getInt(KEY_SAVED_SERVINGS) ?: arguments!!.getInt(KEY_SAVED_SERVINGS)

        val view = View.inflate(activity, R.layout.view_edit_servings, null)
        onClickEditServings(view)
        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.week_at_a_glance_edit_servings_confirm) { _, _ ->
//                    onUpdateMeal(MealPortion(mealPortion.meal, numServings))
                    Snackbar.make(activity!!.findViewById(R.id.week_at_a_glance_recycler_view),
                            R.string.week_at_a_glance_edit_servings_updated, Snackbar.LENGTH_SHORT)
                            .show()
                }
                .setNegativeButton(R.string.week_at_a_glance_edit_servings_cancel) { _, _ ->
                    // Do nothing.
                }
                .create()
    }

    private fun onClickEditServings(view: View) {
        val servingsText = view.findViewById<TextView>(R.id.add_to_plan_servings)
        val plusButton = view.findViewById<ImageView>(R.id.add_to_plan_plus)
        val minusButton = view.findViewById<ImageView>(R.id.add_to_plan_minus)

        servingsText.text = numServings.toString()
        minusButton.isEnabled = numServings != 1

        minusButton.setOnClickListener {
            if (numServings != 1) {
                numServings--
                servingsText.text = numServings.toString()
                minusButton.isEnabled = (numServings > 1)
            }
        }

        plusButton.setOnClickListener {
            numServings++
            minusButton.isEnabled = (numServings > 1)
            servingsText.text = numServings.toString()
        }
    }
}