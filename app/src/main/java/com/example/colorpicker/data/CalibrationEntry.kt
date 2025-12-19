package com.example.colorpicker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calibration")
data class CalibrationEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val concentration: Double,

    val r: Int,
    val g: Int,
    val b: Int,

    val imageUri: String,
    val fileName: String
)
