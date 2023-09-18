package com.exponential_groth.calculator

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager

class ExpActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var parentLayout: ConstraintLayout
    private lateinit var tV1: TextView
    private lateinit var tV2: TextView
    private lateinit var initialPopulation: EditText
    private lateinit var growthRate: EditText
    private lateinit var carryingCapacity: EditText
    private lateinit var numOfYears: EditText
    private lateinit var population: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var buttonReset: Button
    private lateinit var switch: SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exp)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        parentLayout = findViewById(R.id.exp_layout)
        tV1 = findViewById(R.id.textView1)
        tV2 = findViewById(R.id.textView2)
        initialPopulation = findViewById(R.id.editTextInitialPopulation)
        growthRate = findViewById(R.id.editTextGrowthRate)
        carryingCapacity = findViewById(R.id.editTextCarryingCapacity)
        numOfYears = findViewById(R.id.editTextNumberOfYears)
        population = findViewById(R.id.editTextPopulation)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        buttonReset = findViewById(R.id.buttonReset)
        switch = findViewById(R.id.switch_capacity)

        initUI()

        buttonReset.setOnClickListener {
            initialPopulation.setText("")
            growthRate.setText("")
            carryingCapacity.setText("")
            numOfYears.setText("")
            population.setText("")
        }

        buttonCalculate.setOnClickListener {
            val initialPopulationText = initialPopulation.text.toString()
            val growthRateText = growthRate.text.toString()
            val carryingCapacityText = carryingCapacity.text.toString()
            val n = numOfYears.text.toString()
            val populationAfterNYears = population.text.toString()
            val withCapacity = switch.isChecked

            val editTextList = if (withCapacity) listOf(initialPopulation, growthRate, carryingCapacity, numOfYears, population)
            else listOf(initialPopulation, growthRate, numOfYears, population)

            val parameters = if (withCapacity) listOf(initialPopulationText, growthRateText, carryingCapacityText, n, populationAfterNYears)
            else listOf(initialPopulationText, growthRateText, n, populationAfterNYears)

            val missingValueIndex = parameters.indexOfFirst { it.isBlank() }
            if (missingValueIndex != parameters.indexOfLast { it.isBlank() }) {
                Toast.makeText(this, R.string.missing_parameters, Toast.LENGTH_SHORT).show()
            } else if (missingValueIndex == -1) {
                Toast.makeText(this, getString(R.string.no_calculation), Toast.LENGTH_SHORT).show()
            }

            if (withCapacity) {
                editTextList[missingValueIndex].setText(logisticEquation(missingValueIndex, true, parameters.filter { it.isNotBlank() }))
            } else {
                editTextList[missingValueIndex].setText(logisticEquation(missingValueIndex, false, parameters.filter { it.isNotBlank() }))
            }
        }
    }


    private fun initUI() {
        val textColor = sharedPreferences.getInt(getString(R.string.text_color_exp_growth_key), Color.WHITE)
        val colorBackgroundStart = sharedPreferences.getInt(getString(R.string.color_start_background_key), getColor(R.color.startingColorBackground))
        val colorBackgroundEnd = sharedPreferences.getInt(getString(R.string.color_end_background_key), getColor(R.color.endingColorBackground))
        val appBackgroundDrawable = when (sharedPreferences.getString(getString(R.string.orientation_background_key), getString(R.string.t_b)).toString()) {
            getString(R.string.bl_tr) -> {
                GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            getString(R.string.l_r) -> {
                GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            getString(R.string.tl_br) -> {
                GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            else -> {
                GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
        }
        parentLayout.background = appBackgroundDrawable

        listOf(initialPopulation, growthRate, carryingCapacity, numOfYears, population, tV1, tV2).forEach {
            it.setTextColor(textColor)
            it.setHintTextColor(textColor)
        }
    }
}