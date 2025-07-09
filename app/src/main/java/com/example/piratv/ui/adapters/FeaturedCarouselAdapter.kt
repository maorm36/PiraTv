package com.example.piratv.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piratv.databinding.ItemFeaturedBinding
import com.example.piratv.models.FeaturedItem
import com.example.piratv.ui.previewItem.PreviewItemActivity
import com.google.android.material.carousel.MaskableFrameLayout

class FeaturedCarouselAdapter(
    private val context: Context,
    private val items: List<FeaturedItem>
) : RecyclerView.Adapter<FeaturedCarouselAdapter.FeaturedViewHolder>() {

    inner class FeaturedViewHolder(private val binding: ItemFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FeaturedItem) {
            binding.textTitle.text = item.title

            binding.textDescription.text = "" // item.description


            if(item.type.equals("News")){
                Glide.with(binding.imagePoster.context)
                    .load(item.srcImage)
                    .fitCenter()
                    .into(binding.imagePoster)
            }else{
                Glide.with(binding.imagePoster.context)
                    .load("https://image.tmdb.org/t/p/w500" + item.srcImage)
                    .fitCenter()
                    .into(binding.imagePoster)
            }


            binding.root.setOnClickListener {
                val ctx = it.context
                val intent = Intent(ctx, PreviewItemActivity::class.java).apply {
                    putExtra("EXTRA_TITLE", item.title)
                    putExtra("EXTRA_ID",    item.id)
                    putExtra("EXTRA_IMAGE_URL", item.srcImage)
                    putExtra("EXTRA_TYPE", item.type)
                    putExtra("EXTRA_DESCRIPTION", item.description)
                }
                ctx.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFeaturedBinding.inflate(inflater, parent, false)
        // Ensure MaskableFrameLayout is the root view in item_featured.xml
        require(binding.root is MaskableFrameLayout) {
            "The root layout must be a MaskableFrameLayout for Carousel to work."
        }
        return FeaturedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
