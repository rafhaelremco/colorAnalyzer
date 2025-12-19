package com.example.colorpicker.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colorpicker.R
import com.example.colorpicker.data.CalibrationEntry

class CalibrationAdapter(
    private var data: List<CalibrationEntry>,
    private val onDeleteClick: (CalibrationEntry) -> Unit
) : RecyclerView.Adapter<CalibrationAdapter.ViewHolder>() {

    fun updateData(newData: List<CalibrationEntry>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calibration, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.tvFileName.text = item.fileName
        holder.tvValue.text =
            "Konsentrasi: ${item.concentration}\nR:${item.r} G:${item.g} B:${item.b}"

        try {
            val uri = Uri.parse(item.imageUri)
            val inputStream =
                holder.itemView.context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            holder.imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            holder.imageView.setImageResource(android.R.color.darker_gray)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgPreview)
        val tvFileName: TextView = view.findViewById(R.id.tvFileName)
        val tvValue: TextView = view.findViewById(R.id.tvValue)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
}
