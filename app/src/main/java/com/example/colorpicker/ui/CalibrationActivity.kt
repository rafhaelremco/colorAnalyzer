package com.example.colorpicker.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.colorpicker.R
import com.example.colorpicker.data.AppDatabase
import com.example.colorpicker.data.CalibrationEntry
import com.example.colorpicker.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalibrationActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var tvInfo: TextView
    private lateinit var etConcentration: EditText

    private var selectedImageUri: Uri? = null
    private var fileName: String = ""

    private var rSample = 0
    private var rControl = 0
    private var deltaR = 0
    private var bitmapReady = false

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                fileName = getFileName(uri)

                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)

                    val (r, _, _) = ImageUtils.calculateMeanRGB(bitmap)
                    rSample = r

                    // R_kontrol diasumsikan dari strip referensi (sementara = nilai R terendah)
                    rControl = rSample  // akan dikoreksi global saat prediksi
                    deltaR = rSample - rControl

                    tvInfo.text =
                        "R sampel  : $rSample\n" +
                                "ΔR        : $deltaR"

                    bitmapReady = true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

        imageView = findViewById(R.id.imagePreview)
        tvInfo = findViewById(R.id.tvRgbValue)
        etConcentration = findViewById(R.id.etConcentration)

        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            imagePicker.launch("image/*")
        }

        findViewById<Button>(R.id.btnNext).setOnClickListener {

            if (!bitmapReady || selectedImageUri == null) {
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val concentrationText = etConcentration.text.toString()
            if (concentrationText.isBlank()) {
                Toast.makeText(this, "Masukkan nilai konsentrasi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SIMPAN R SAJA (ΔR dihitung saat prediksi)
            val entry = CalibrationEntry(
                concentration = concentrationText.toDouble(),
                r = rSample,
                g = 0,
                b = 0,
                imageUri = selectedImageUri.toString(),
                fileName = fileName
            )

            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase
                    .getInstance(applicationContext)
                    .calibrationDao()
                    .insert(entry)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CalibrationActivity,
                        "Kalibrasi (ΔR) berhasil disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }
}
