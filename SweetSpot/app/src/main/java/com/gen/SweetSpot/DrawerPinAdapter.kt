package com.gen.SweetSpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DrawerPinAdapter(
    private var userPins: List<UserPin>,
    private val itemClickListener: (UserPin) -> Unit
) : RecyclerView.Adapter<DrawerPinAdapter.DrawerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_drawer_pin, parent, false)
        return DrawerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        val userPin = userPins[position]
        holder.bind(userPin)
    }

    override fun getItemCount(): Int = userPins.size

    fun updateData(newUserPins: List<UserPin>) {
        userPins = newUserPins
        notifyDataSetChanged()
    }

    inner class DrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pinLabel: TextView = itemView.findViewById(R.id.drawer_pin_label)
        private val pinIcon: ImageView = itemView.findViewById(R.id.drawer_pin_icon)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener(userPins[position])
                }
            }
        }

        fun bind(userPin: UserPin) {
            pinLabel.text = userPin.label
        }
    }
}