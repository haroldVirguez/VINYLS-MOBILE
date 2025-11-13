package com.team3.vinyls.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.R
import com.team3.vinyls.ui.models.MusicianUiModel
import com.team3.vinyls.databinding.ItemMusicianBinding
import kotlinx.coroutines.*
import com.bumptech.glide.Glide


class MusiciansAdapter(private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main) :
    RecyclerView.Adapter<MusiciansAdapter.MusicianViewHolder>() {

    private val items = mutableListOf<MusicianUiModel>()

    fun submitList(newItems: List<MusicianUiModel>) {
        items.clear()
        items.addAll(newItems)
        try {
            notifyDataSetChanged()
        } catch (_: Exception) { /* ignore for unit tests */
        }
    }

    var onMusicianClick: ((MusicianUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val binding =
            ItemMusicianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicianViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: MusicianViewHolder) {
        super.onViewRecycled(holder)
        holder.loadJob?.cancel()
    }

    override fun getItemCount(): Int = items.size

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var loadJob: Job? = null

        fun bind(item: MusicianUiModel) {
            binding.txtName.text = item.name
            binding.txtSubtitle.text = item.subtitle

            loadJob?.cancel()

            Glide.with(binding.root.context)
                .load(item.image)
                .centerCrop()
                .into(binding.imgArtist)

            binding.root.setOnClickListener { onMusicianClick?.invoke(item) }
        }
    }
}
