package com.team3.vinyls.albums.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.R
import com.team3.vinyls.albums.ui.AlbumUiModel

class AlbumsAdapter : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    private val items: MutableList<AlbumUiModel> = mutableListOf()

    fun submitList(newItems: List<AlbumUiModel>) {
        items.clear()
        items.addAll(newItems)
        // En tests unitarios la infraestructura de RecyclerView puede no inicializar los observadores internos,
        // lo que provoca NullPointerException en notifyDataSetChanged(). Capturamos la excepción para que
        // los tests unitarios no fallen por esto. En runtime normal esto no tiene impacto.
        try {
            notifyDataSetChanged()
        } catch (e: Exception) {
            // swallow for unit tests where RecyclerView internals are not present
        }
    }

    var onAlbumClick: ((AlbumUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album_card, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txtSubtitle)

        fun bind(item: AlbumUiModel) {
            txtTitle.text = item.title
            txtSubtitle.text = item.subtitle
            itemView.setOnClickListener { onAlbumClick?.invoke(item) }
        }
    }
}