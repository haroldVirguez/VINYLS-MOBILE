package com.team3.vinyls.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.R
import com.team3.vinyls.ui.models.AlbumUiModel
import kotlinx.coroutines.*
import java.net.URL

class AlbumsAdapter(private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main) : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    private val items = mutableListOf<AlbumUiModel>()

    fun submitList(newItems: List<AlbumUiModel>) {
        items.clear()
        items.addAll(newItems)
        try {
            notifyDataSetChanged()
        } catch (_: Exception) { /* ignore for unit tests */ }
    }

    var onAlbumClick: ((AlbumUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album_card, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: AlbumViewHolder) {
        super.onViewRecycled(holder)
        holder.loadJob?.cancel() // stop background work for recycled views
    }
    override fun getItemCount(): Int = items.size

    // 🧠 Use ViewHolder-level coroutine scope
    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txtSubtitle)
        private val txtGenre: TextView = itemView.findViewById(R.id.txtGenre)

        var loadJob: Job? = null  // reference to cancel previous loads

        fun bind(item: AlbumUiModel) {
            txtTitle.text = item.title
            txtSubtitle.text = item.subtitle
            txtGenre.text = item.genre

            // cancel any previous running job for this recycled view
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

                }
            }

            itemView.setOnClickListener { onAlbumClick?.invoke(item) }
        }
    }
}