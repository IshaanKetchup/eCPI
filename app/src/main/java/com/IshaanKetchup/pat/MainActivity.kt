package com.IshaanKetchup.pat

//CHEETAH
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar

import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var editTextQNH: EditText
    private lateinit var editTextElevation: EditText
    private lateinit var editTextTemperature: EditText
    private lateinit var editTextPower: EditText
    private lateinit var textViewWeight: TextView
    private lateinit var editTextWeight: EditText
    private lateinit var editTextPressureAltitude: EditText
    private lateinit var textViewDensityAltitude: TextView
    private lateinit var textViewMaxPower: TextView
    private lateinit var textViewMaxWeight: TextView
    private lateinit var textViewCruisePower: TextView
    private lateinit var textViewHover: TextView
    private lateinit var buttonCalculate: Button
    private lateinit var buttonReset: Button
    private lateinit var buttonClear: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Drawer Layout + Navigation
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_first_activity)   // In MainActivity
        navigationView.alpha = 0.95f

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_first_activity -> {
                    //drawerLayout.closeDrawers()  // Already here
                    true
                }
                R.id.nav_second_activity -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
        navigationView.setCheckedItem(R.id.nav_first_activity)

        // Initialize UI widgets
        editTextQNH = findViewById(R.id.editTextQNH)
        editTextElevation = findViewById(R.id.editTextElevation)
        editTextTemperature = findViewById(R.id.editTextTemperature)
        editTextWeight = findViewById(R.id.editTextWeight)
        textViewWeight = findViewById(R.id.textViewWeight)
        editTextPressureAltitude  = findViewById(R.id.editTextPressureAltitude)
        editTextPower = findViewById(R.id.editTextPower)
        textViewDensityAltitude = findViewById(R.id.textViewDensityAltitude)
        textViewMaxPower = findViewById(R.id.textViewMaxPower)
        textViewMaxWeight = findViewById(R.id.textViewMaxWeight)
        textViewHover = findViewById(R.id.textViewHover)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        buttonReset = findViewById(R.id.buttonReset)
        buttonClear = findViewById(R.id.buttonClear)
        textViewCruisePower = findViewById(R.id.textViewCruisePower)
        // Button listeners
        buttonCalculate.setOnClickListener {
                calculateAndDisplay()
        }
        buttonReset.setOnClickListener {
            resetInputs()
        }

        buttonClear.setOnClickListener{
            clearInputs()
        }



        editTextQNH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull()
                if (input != null) {
                    if (input < 983 || input > 1043) {
                        editTextQNH.error = "QNH must be between 983 and 1043"
                    } else {
                        editTextQNH.error = null
                        val qnh = editTextQNH.text.toString().toDouble()
                        val elevation = editTextElevation.text.toString().toDoubleOrNull()
                        if (elevation != null && elevation in -1000.0..22000.0) {
                            val pressureAltitude = calculatePressureAltitude(elevation, qnh)
                            editTextPressureAltitude.setText(String.format("%.2f", pressureAltitude))
                        }
                    }
                } else if (!s.isNullOrEmpty()) {
                    editTextQNH.error = "Invalid number"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editTextElevation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull()
                if (input != null) {
                    if (input < -1000 || input > 22000) {
                        editTextElevation.error = "Elevation must be between -1000 and 22000"
                    } else {
                        editTextElevation.error = null
                        val qnh = editTextQNH.text.toString().toDoubleOrNull()
                        val elevation = editTextElevation.text.toString().toDouble()
                        if (qnh != null && qnh in 983.0..1043.0) {
                            val pressureAltitude = calculatePressureAltitude(elevation, qnh)
                            editTextPressureAltitude.setText(String.format("%.2f", pressureAltitude))
                        }
                    }
                } else if (!s.isNullOrEmpty()) {
                    editTextElevation.error = "Invalid number"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editTextPower.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull()
                if (input != null) {
                    if (input < 0 || input > 1.0) {
                        editTextPower.error = "Value must be between 0.00 and 1.0"
                    } else {
                        editTextPower.error = null
                        updateWeightDynamic()
                    }
                } else if (!s.isNullOrEmpty()) {
                    editTextPower.error = "Invalid number"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        editTextTemperature.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull()
                if (input != null) {
                    if (input < -30 || input > 40) {
                        editTextTemperature.error = "Temperature must be between -30 and 40"
                    } else {
                        editTextTemperature.error = null  // clear error if valid
                        updateWeightDynamic() // Only call this if input is valid
                    }
                } else if (!s.isNullOrEmpty()) {
                    editTextTemperature.error = "Invalid number"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editTextPressureAltitude.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull()

                if (input != null) {
                    if (input < -1000 || input > 22000) {
                        editTextPressureAltitude.error = "Pressure Alt must be between -1000 and 22000"
                    } else {
                        editTextPressureAltitude.error = null  // clear error if valid
                        updateWeightDynamic() // Only call this if input is valid
                    }
                } else if (!s.isNullOrEmpty()) {
                    editTextPressureAltitude.error = "Invalid number"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val editTextQNH = findViewById<TextInputEditText>(R.id.editTextQNH)
        val textInputLayoutQNH = findViewById<TextInputLayout>(R.id.textInputLayoutQNH)
        val originalIcon = textInputLayoutQNH.startIconDrawable // save the original icon


        //  FOCUS LISTENERS
        editTextQNH.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hide the icon when focused
                textInputLayoutQNH.startIconDrawable = null
            } else {
                // Restore the icon when focus lost
                textInputLayoutQNH.startIconDrawable = originalIcon
            }
        }

        val editTextElevation = findViewById<TextInputEditText>(R.id.editTextElevation)
        val textInputLayoutElevation = findViewById<TextInputLayout>(R.id.textInputElevation)
        val originalIconElevation = textInputLayoutElevation.startIconDrawable

        editTextElevation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hide icon when focused
                textInputLayoutElevation.startIconDrawable = null
            } else {
                // Restore icon when focus lost
                textInputLayoutElevation.startIconDrawable = originalIconElevation
            }
        }

        val editTextPressureAltitude = findViewById<TextInputEditText>(R.id.editTextPressureAltitude)
        val textInputLayoutPressureAltitude = findViewById<TextInputLayout>(R.id.textInputPressureAltitude)
        val originalIconPressureAltitude = textInputLayoutPressureAltitude.startIconDrawable

        editTextPressureAltitude.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutPressureAltitude.startIconDrawable = null
            } else {
                textInputLayoutPressureAltitude.startIconDrawable = originalIconPressureAltitude
            }
        }

        val editTextTemperature = findViewById<TextInputEditText>(R.id.editTextTemperature)
        val textInputLayoutTemperature = findViewById<TextInputLayout>(R.id.textInputTemperature)
        val originalIconTemperature = textInputLayoutTemperature.startIconDrawable

        editTextTemperature.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutTemperature.startIconDrawable = null
            } else {
                textInputLayoutTemperature.startIconDrawable = originalIconTemperature
            }
        }

        val editTextWeight = findViewById<TextInputEditText>(R.id.editTextWeight)
        val textInputLayoutWeight = findViewById<TextInputLayout>(R.id.textInputWeight)
        val originalIconWeight = textInputLayoutWeight.startIconDrawable

        editTextWeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutWeight.startIconDrawable = null
            } else {
                textInputLayoutWeight.startIconDrawable = originalIconWeight
            }
        }



    }

    private fun getPressureAltitude(): Double? {
        val paInput = editTextPressureAltitude.text.toString().toDoubleOrNull()
        if (paInput != null) return paInput

        val qnh = editTextQNH.text.toString().toDoubleOrNull()
        val elevation = editTextElevation.text.toString().toDoubleOrNull()

        // Check if both values are valid
        return if (qnh != null && elevation != null) {
            calculatePressureAltitude(elevation, qnh) // Replace with your actual method
        } else {
            null
        }
    }

    private fun updateWeightDynamic() {
        val power = editTextPower.text.toString().toDoubleOrNull()
        val pressureAltitude = getPressureAltitude()
        val temperature = editTextTemperature.text.toString().toDoubleOrNull()

        // Check for valid inputs
        if (
            power == null || power < 0.0 || power > 1.0 ||
            pressureAltitude == null ||
            temperature == null || temperature < -30.0 || temperature > 40.0
        ) {
            textViewWeight.text = "" // Clear output if any input is invalid/missing
            return
        }

        val pa = interpolatePressure(pressureAltitude)
        var currWeight = (power * pa * 288.0 * 6600.0) / (1013.0 * (temperature + 273.0))
        if (currWeight > 4850.0) currWeight = 4850.0
        textViewWeight.text = String.format("%.2f", currWeight)
    }



    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    data class LookupEntry(val altitude: Int, val pressure: Double, val mbToFt: Int?)
        val lookupTable: List<LookupEntry> = listOf(
            LookupEntry(-1000, 1050.35353936301, 27),
            LookupEntry(0, 1013.20, 28),
            LookupEntry(1000, 977.147660901616, 29),
            LookupEntry(2000, 942.100765523108, 29),
            LookupEntry(3000, 908.107192136849, 30),
            LookupEntry(4000, 875.071184198091, 31),
            LookupEntry(5000, 843.0884982515839, 32),
            LookupEntry(6000, 812.0154994802, 33),
            LookupEntry(7000, 781.900066156318, 34),
            LookupEntry(8000, 752.694320007561, 35),
            LookupEntry(9000, 724.350382761554, 36),
            LookupEntry(10000, 696.916132690672, 38),
            LookupEntry(11000, 670.34369152254, 39),
            LookupEntry(12000, 644.537302712409, 40),
            LookupEntry(13000, 619.592722805028, 41),
            LookupEntry(14000, 595.414195255647, 43),
            LookupEntry(15000, 572.049598336641, 44),
            LookupEntry(16000, 549.403175503261, 46),
            LookupEntry(17000, 527.474926755505, 47),
            LookupEntry(18000, 506.31273036575, 49),
            LookupEntry(19000, 485.820829789245, 50),
            LookupEntry(20000, 465.99922502599, 52),
            LookupEntry(21000, 446.847916075985, 54),
            LookupEntry(22000, 428.319024666856, 56),
            LookupEntry(23000, 410.412550798601, null)
        )
        fun getNearest1000(value: Int): Int = 1000 * ((value + 500) / 1000)
        fun getLower1000(value: Int): Int = 1000 * (value / 1000)

    val pressureMap = lookupTable.associate { it.altitude to it.pressure }
    val conversionMap = lookupTable.filter { it.mbToFt != null }.associate { it.altitude to it.mbToFt!! }

    fun interpolatePressure(pressureAltitude: Double): Double {
        val baseAlt = getLower1000(pressureAltitude.toInt())
        val nextAlt = baseAlt + 1000
        val b = pressureMap[baseAlt] ?: error("Out of bounds")
        val bNext = pressureMap[nextAlt] ?: error("Out of bounds")
        val bPrime = b - bNext
        return b - ((pressureAltitude - baseAlt) / 1000.0) * bPrime
    }

    fun calculatePressureAltitude(elevationFt: Double, qnhMb: Double): Double {
        val nearest = getNearest1000(elevationFt.toInt())
        val c = conversionMap[nearest] ?: error("Out of bounds")
        return elevationFt + (qnhMb - 1013) * c
    }

    @SuppressLint("SetTextI18n")
    private fun calculateAndDisplay() {
        try{
            val qnh = editTextQNH.text.toString().toDoubleOrNull()
            if(qnh != null){
                if(qnh < 983 || qnh > 1043){
                    throw IllegalArgumentException("QNH out of range: must be between 0.0 and 1.0")
                }
            }

            val elevation = editTextElevation.text.toString().toDoubleOrNull()
            //-1000 to 22000
            if (elevation != null) {
                if(elevation < -1000.0 || elevation > 22000.0){
                    throw IllegalArgumentException("Elevation out of range: must be between -1000 and 22000")
                }
            }

            val temperature = editTextTemperature.text.toString().toDoubleOrNull()
            //-30 to 40
            if (temperature != null) {
                if(temperature < -30 || temperature > 40){
                    throw IllegalArgumentException("Temperature out of range: must be between -30 and 40")
                }
            }
            else{
                throw IllegalArgumentException("Temperature Value Required")
            }


            val pressureAltitudeInput = editTextPressureAltitude.text.toString().toDoubleOrNull()
            val weightInput = editTextWeight.text.toString()
            val givenWeight = weightInput.toDoubleOrNull()


            // PRESSURE ALTITUDE CALCULATION
            val pressureAltitude = if(qnh != null && elevation != null) {
                val calculatedPA = calculatePressureAltitude(elevation, qnh)
                editTextPressureAltitude.setText("%.2f".format(calculatedPA))
                calculatedPA
            }
            else pressureAltitudeInput ?: throw IllegalArgumentException("Pressure Altitude input required if QNH and Elevation empty")

            if(pressureAltitude>22000.0 || pressureAltitude<-1000){
                throw IllegalArgumentException("Pressure Alt out of range: must be between -1000 and 22000")
            }

            // Pa CALCULATION
            val pa = interpolatePressure(pressureAltitude);

            // DENSITY ALTITUDE CALCULATION
            val densityAltitude = pressureAltitude + 120.0 * (temperature - (15.0 - (2.0 * pressureAltitude / 1000.0)))
            val stepCount = (densityAltitude / 660.0).toInt()

            var maxPowerAvailable = 0.8 + (stepCount * 0.01)
            if (maxPowerAvailable > 1.0) {
                maxPowerAvailable = 1.0
            }

            // CRUISE POWER CALCULATION

            var cruisePower = maxPowerAvailable
            if (cruisePower > 0.85) {
                cruisePower = 0.85
            }

            //minWeight = ??? (2600)
            var maxWeight = (maxPowerAvailable * pa * 288 * 6600.0) / (1013*(temperature+273))
            if (maxWeight>5070) maxWeight=5070.0
            // UI display
            textViewCruisePower.text = "%.2f".format(cruisePower)
            textViewDensityAltitude.text = "%.2f ft".format(densityAltitude)
            textViewMaxPower.text = "%.2f".format(maxPowerAvailable)

            if (givenWeight != null) {
                if (maxWeight < givenWeight) {
                    textViewMaxWeight.text = "%.2f lbs - NOT POSSIBLE".format(maxWeight)
                }

                //val powerToHover = givenWeight * (1013.0 / qnh) * ((temperature + 273.0) / 288.0) * (1 / 6820.0)
                val powerToHover = (givenWeight * 1013.0 * (temperature + 273.0)) / (pa * 288.0 * 6600.0)
                textViewHover.text = if (powerToHover > maxPowerAvailable) {
                    "%.2f - NOT POSSIBLE".format(powerToHover)
                } else {
                    "%.2f".format(powerToHover)
                }
            } else {
                textViewHover.text = " "
            }
            if(maxWeight < 4300){
                textViewMaxWeight.text = "%.2f lbs".format(maxWeight)
            }
            else{
                textViewMaxWeight.text = "%.2f lbs (%.0f kg Jettisonable) ".format(maxWeight, (maxWeight-4300)/2.2)
            }
        }
        catch(e: Exception){
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetInputs() {
        editTextQNH.text.clear()
        editTextElevation.text.clear()
        editTextTemperature.setText("25.0")
        editTextWeight.text.clear()
        editTextPressureAltitude.setText("0.0")
        editTextPower.setText("0.90")
        textViewWeight.text = " "
        // Also clear output fields if desired:
    }

    private fun clearInputs() {
        editTextQNH.text.clear()
        editTextElevation.text.clear()
        editTextTemperature.text.clear()
        editTextWeight.text.clear()
        editTextPressureAltitude.text.clear()
        editTextPower.text.clear()
        textViewWeight.text = " "
        textViewCruisePower.text = " "
        textViewDensityAltitude.text = " "
        textViewMaxPower.text = " "
        textViewMaxWeight.text = " "
        textViewHover.text = " "
        // Also clear output fields if desired:
    }


// Optional: Override back press to close drawer if open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
