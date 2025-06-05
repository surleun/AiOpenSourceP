package com.gen.SweetSpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(
    private val comments: MutableList<CommentItem>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: CommentItem) {
        comments.add(0, comment)
        notifyItemInserted(0)
    }

    fun updateComments(newComments: List<CommentItem>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentAuthor: TextView = itemView.findViewById(R.id.commentAuthor)
        private val commentTimestamp: TextView = itemView.findViewById(R.id.commentTimestamp)
        private val commentContent: TextView = itemView.findViewById(R.id.commentContent)
        private val commentLikeButton: ImageButton = itemView.findViewById(R.id.commentLikeButton)
        private val commentLikeCount: TextView = itemView.findViewById(R.id.commentLikeCount)
        private val commentReportButton: ImageButton = itemView.findViewById(R.id.commentReportButton)

        fun bind(comment: CommentItem) {
            commentAuthor.text = comment.authorNickname
            commentTimestamp.text = comment.createdAt
            commentContent.text = comment.content
            commentLikeCount.text = comment.likeCount.toString()

            commentLikeButton.setOnClickListener {
                Toast.makeText(itemView.context, "${comment.authorNickname} 댓글 좋아요!", Toast.LENGTH_SHORT).show()
            }
            commentReportButton.setOnClickListener {
                Toast.makeText(itemView.context, "${comment.authorNickname} 댓글 신고", Toast.LENGTH_SHORT).show()
            }
        }
    }
}