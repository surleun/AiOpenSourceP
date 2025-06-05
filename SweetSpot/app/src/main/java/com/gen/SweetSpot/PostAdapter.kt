package com.gen.SweetSpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter(
    private var posts: List<PostItem>,
    private val itemClickListener: OnPostItemClickListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface OnPostItemClickListener {
        fun onPostItemClicked(post: PostItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_board_item_list, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    fun updateData(newPosts: List<PostItem>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postImage: ShapeableImageView = itemView.findViewById(R.id.post_image)
        private val postTitle: TextView = itemView.findViewById(R.id.post_title)
        private val postAuthorNickname: TextView = itemView.findViewById(R.id.post_author_nickname)
        private val postCommentsCount: TextView = itemView.findViewById(R.id.post_comments_count)
        private val postRecommendationsCount: TextView = itemView.findViewById(R.id.post_recommendations_count)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onPostItemClicked(posts[position])
                }
            }
        }

        fun bind(post: PostItem) {
            postTitle.text = post.title
            postAuthorNickname.text = post.authorNickname
            postCommentsCount.text = post.commentCount.toString()
            postRecommendationsCount.text = post.likeCount.toString()

            if (!post.imageUrl.isNullOrEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(post.imageUrl)
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