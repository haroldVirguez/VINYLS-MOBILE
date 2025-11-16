package com.team3.vinyls.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.ui.models.MusicianUiModel
import com.team3.vinyls.databinding.ItemMusicianBinding

class MusiciansAdapter(
    private val uiDispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main,
    private val imageLoader: ImageLoader = GlideImageLoader()
) : ListAdapter<MusicianUiModel, MusiciansAdapter.MusicianViewHolder>(MusicianDiffCallback()) {

    var onMusicianClick: ((MusicianUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val binding = ItemMusicianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicianViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: MusicianViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MusicianUiModel) {
            binding.txtName.text = item.name
            binding.txtSubtitle.text = item.subtitle

            imageLoader.load(item.image, binding.imgArtist)

            binding.root.setOnClickListener { onMusicianClick?.invoke(item) }
        }

        fun clear() {
            imageLoader.clear(binding.imgArtist)
        }
    }

    class MusicianDiffCallback : DiffUtil.ItemCallback<MusicianUiModel>() {
        override fun areItemsTheSame(oldItem: MusicianUiModel, newItem: MusicianUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MusicianUiModel, newItem: MusicianUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
