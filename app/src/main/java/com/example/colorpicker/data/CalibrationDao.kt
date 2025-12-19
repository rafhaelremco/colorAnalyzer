package com.example.colorpicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CalibrationDao {

    @Insert
    suspend fun insert(entry: CalibrationEntry)

    @Query("SELECT * FROM calibration ORDER BY id DESC")
    suspend fun getAll(): List<CalibrationEntry>

    @Query("DELETE FROM calibration WHERE id = :id")
    suspend fun deleteById(id: Long)
}
