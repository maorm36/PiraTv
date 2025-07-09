package com.example.piratv.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piratv.databinding.ItemSearchResultBinding
import com.example.piratv.models.TitleResult

class SearchResultAdapter(
    private val items: List<TitleResult>,
    private val onClick: (TitleResult) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSearchResultBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TitleResult) {
            // load the poster
            Glide.with(binding.posterImage.context)
                .load("https://image.tmdb.org/t/p/w500" + item.primaryImage)
                .centerCrop()
                .into(binding.posterImage)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
