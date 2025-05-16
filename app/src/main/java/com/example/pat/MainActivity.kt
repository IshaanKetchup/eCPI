package com.example.pat

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextQNH: EditText
    private lateinit var editTextElevation: EditText
    private lateinit var editTextTemperature: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var textViewPressureAltitude: TextView
    private lateinit var textViewDensityAltitude: TextView
    private lateinit var textViewMaxPower: TextView
    private lateinit var textViewMaxWeight: TextView
    private lateinit var textViewHover: TextView
    private lateinit var buttonCalculate: Button

    private val DEFAULTQNH = 1013.0
    private val DEFAULTELEVATION = 0.0
    private val DEFAULTTEMPERATURE = 15.0
    private val DEFAULTWEIGHT = 4500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextQNH = findViewById(R.id.editTextQNH)
        editTextElevation = findViewById(R.id.editTextElevation)
        editTextTemperature = findViewById(R.id.editTextTemperature)
        textViewPressureAltitude = findViewById(R.id.textViewPressureAltitude)
        textViewDensityAltitude = findViewById(R.id.textViewDensityAltitude)
        textViewMaxPower = findViewById(R.id.textViewMaxPower)
        textViewMaxWeight = findViewById(R.id.textViewMaxWeight)
        textViewHover = findViewById(R.id.textViewHover)
        editTextWeight = findViewById(R.id.editTextWeight)
        buttonCalculate = findViewById(R.id.buttonCalculate)

        buttonCalculate.setOnClickListener {
            calculateAndDisplay()

        }

    }

    @SuppressLint("SetTextI18n")
    private fun calculateAndDisplay() {
        val qnh = editTextQNH.text.toString().toDoubleOrNull() ?: DEFAULTQNH
        val elevation = editTextElevation.text.toString().toDoubleOrNull() ?: DEFAULTELEVATION
        val temperature = editTextTemperature.text.toString().toDoubleOrNull() ?: DEFAULTTEMPERATURE
        val givenWeight = editTextWeight.text.toString().toDoubleOrNull() ?: DEFAULTWEIGHT.toDouble()


        val pressureAltitude = elevation + (1013.0 - qnh) * 30.0
        val densityAltitude = pressureAltitude + 120.0 * (temperature - (15.0 - (2.0 * pressureAltitude / 1000.0)))
        val stepCount = (densityAltitude / 660.0).toInt()
        val maxPowerAvailable = 0.75 + (stepCount * 0.01)

        val maxWeight = maxPowerAvailable * (qnh / 1013.0) * (288.0 / (temperature + 273.0)) * 6600.0

        val powerToHover = givenWeight * (1013.0 / qnh) * ((temperature + 273.0) / 288.0) * (1/ 6600.0)

        textViewPressureAltitude.text = "Pressure Altitude: %.2f ft".format(pressureAltitude)
        textViewDensityAltitude.text = "Density Altitude: %.2f ft".format(densityAltitude)
        textViewMaxPower.text = "Max Power Available: %.2f".format(maxPowerAvailable)
        //textViewMaxWeight.text = "Max Weight: %.2f lbs".format(maxWeight)
        //textViewHover.text = "Power to Hover: %.2f ".format(powerToHover)
        if (maxWeight < givenWeight) {
            textViewMaxWeight.text = "Max Weight (%.2f) < Current Weight (%.2f) --- NOT POSSIBLE".format(maxWeight, givenWeight)
        } else {
            textViewMaxWeight.text = "Max Weight: %.2f lbs".format(maxWeight)
        }

        // Check 2: Power to Hover exceeds Max Power Available
        if (powerToHover > maxPowerAvailable) {
            textViewHover.text = "Power to Hover (%.2f) > Max Power (%.2f) --- NOT POSSIBLE".format(powerToHover, maxPowerAvailable)
        } else {
            textViewHover.text = "Power to Hover: %.2f".format(powerToHover)
        }
    }
    private fun clearInputs() {
        editTextQNH.text.clear()
        editTextElevation.text.clear()
        editTextTemperature.text.clear()
        editTextWeight.text.clear()
    }

}