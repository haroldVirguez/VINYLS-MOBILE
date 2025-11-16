package com.team3.vinyls.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.R
import com.team3.vinyls.ui.models.AlbumUiModel
import com.bumptech.glide.Glide

interface ImageLoader {
    fun load(url: String?, target: ImageView)
    fun clear(target: ImageView)
}

class GlideImageLoader(private val placeholderRes: Int = R.drawable.vinyls_card_bg) : ImageLoader {
    override fun load(url: String?, target: ImageView) {
        if (!url.isNullOrEmpty()) {
            Glide.with(target.context)
                .load(url)
                .placeholder(placeholderRes)
                .centerCrop()
                .into(target)
        } else {
            target.setImageResource(placeholderRes)
        }
    }

    override fun clear(target: ImageView) {
        Glide.with(target.context).clear(target)
        target.setImageResource(placeholderRes)
    }
}

class AlbumsAdapter(
    private val uiDispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main,
    private val imageLoader: ImageLoader = GlideImageLoader()
) : ListAdapter<AlbumUiModel, AlbumsAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    var onAlbumClick: ((AlbumUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album_card, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: AlbumViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        private val txtGenre: TextView = itemView.findViewById(R.id.txtGenre)

        fun bind(item: AlbumUiModel) {
            txtTitle.text = item.title
            txtSubtitle.text = item.subtitle
            txtGenre.text = item.genre

            // Load image using injected ImageLoader (default: GlideImageLoader)
            imageLoader.load(item.cover, imgCover)

            itemView.setOnClickListener { onAlbumClick?.invoke(item) }
        }

        fun clear() {
            imageLoader.clear(imgCover)
        }
    }

    class AlbumDiffCallback : DiffUtil.ItemCallback<AlbumUiModel>() {
        override fun areItemsTheSame(oldItem: AlbumUiModel, newItem: AlbumUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlbumUiModel, newItem: AlbumUiModel): Boolean {
            return oldItem == newItem
        }
    }
}