package com.example.colorpicker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorpicker.R
import com.example.colorpicker.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalibrationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalibrationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration_list)

        recyclerView = findViewById(R.id.recyclerCalibration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CalibrationAdapter(emptyList()) { entry ->
            deleteEntry(entry.id)
        }

        recyclerView.adapter = adapter
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = AppDatabase
                .getInstance(applicationContext)
                .calibrationDao()
                .getAll()

            withContext(Dispatchers.Main) {
                adapter.updateData(data)
            }
        }
    }

    private fun deleteEntry(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase
                .getInstance(applicationContext)
                .calibrationDao()
                .deleteById(id)

            loadData()
        }
    }
}
