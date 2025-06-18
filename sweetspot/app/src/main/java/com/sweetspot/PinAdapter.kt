package com.sweetspot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PinAdapter(
    private var pins: List<PinItem>,
    private val itemClickListener: OnPinItemClickListener
) : RecyclerView.Adapter<PinAdapter.PinViewHolder>() {

    interface OnPinItemClickListener {
        fun onPinItemClicked(pin: PinItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_board_item_list, parent, false)
        return PinViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinViewHolder, position: Int) {
        val pin = pins[position]
        holder.bind(pin)
    }

    override fun getItemCount(): Int = pins.size

    fun updateData(newPins: List<PinItem>) {
        pins = newPins
        notifyDataSetChanged()
    }

    inner class PinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postImage: ShapeableImageView = itemView.findViewById(R.id.post_image)
        private val postTitle: TextView = itemView.findViewById(R.id.post_title)
        private val postAuthorNickname: TextView = itemView.findViewById(R.id.post_author_nickname)
        private val postCommentsCount: TextView = itemView.findViewById(R.id.post_comments_count)
        private val postRecommendationsCount: TextView = itemView.findViewById(R.id.post_recommendations_count)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onPinItemClicked(pins[position])
                }
            }
        }

        fun bind(pin: PinItem) {
            postTitle.text = pin.title
            postAuthorNickname.text = pin.authorNickname
            postCommentsCount.text = pin.commentCount.toString()
            postRecommendationsCount.text = pin.likeCount.toString()

            if (!pin.imageUrl.isNullOrEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(pin.imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(postImage)
            } else {
                postImage.setImageResource(R.drawable.ic_placeholder_image)
                postImage.visibility = View.VISIBLE
            }
        }
    }
}