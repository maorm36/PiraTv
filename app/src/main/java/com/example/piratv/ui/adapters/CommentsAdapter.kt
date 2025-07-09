package com.example.piratv.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.piratv.databinding.ItemCommentBinding
import com.example.piratv.models.Comment
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.VH>() {

    private val items = mutableListOf<Comment>()

    inner class VH(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(c: Comment) {
            binding.textCommentUser.text = c.userId
            binding.textCommentBody.text = c.text
            binding.textCommentRating.text  = "Rating: ${c.rating}"
            val fmt = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            binding.textCommentWhen.text   = fmt.format(c.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun update(newItems: List<Comment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}