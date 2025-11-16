package com.team3.vinyls.ui.adapters

import android.graphics.BitmapFactory
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
import kotlinx.coroutines.*
import java.net.URL

class AlbumsAdapter(private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main) : ListAdapter<AlbumUiModel, AlbumsAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

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
        holder.loadJob?.cancel()
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        private val txtGenre: TextView = itemView.findViewById(R.id.txtGenre)

        var loadJob: Job? = null

        fun bind(item: AlbumUiModel) {
            txtTitle.text = item.title
            txtSubtitle.text = item.subtitle
            txtGenre.text = item.genre

            loadJob?.cancel()

            // launch new coroutine for this holder
            loadJob = CoroutineScope(uiDispatcher).launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        val url = URL(item.cover)
                        BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    }
                    imgCover.setImageBitmap(bitmap)
                } catch (e: CancellationException) {
                    // ignore if the job was cancelled
                } catch (e: Exception) {
                    imgCover.setImageResource(R.drawable.vinyls_card_bg)
                }
            }

            itemView.setOnClickListener { onAlbumClick?.invoke(item) }
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