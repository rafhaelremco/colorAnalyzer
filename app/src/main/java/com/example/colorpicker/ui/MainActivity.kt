package com.example.colorpicker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.colorpicker.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnCalibration).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    com.example.colorpicker.ui.CalibrationActivity::class.java
                )
            )
        }

        findViewById<Button>(R.id.btnList).setOnClickListener {
            startActivity(Intent(this, CalibrationListActivity::class.java))
        }

        findViewById<Button>(R.id.btnPrediction).setOnClickListener {
            startActivity(Intent(this, PredictionActivity::class.java))
        }

    }
}
