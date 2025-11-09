package com.team3.vinyls.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team3.vinyls.R
import com.team3.vinyls.ui.MusicianUiModel
import com.team3.vinyls.databinding.ItemMusicianBinding
import kotlinx.coroutines.*
import java.net.URL

class MusiciansAdapter(private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main) : RecyclerView.Adapter<MusiciansAdapter.MusicianViewHolder>() {

    private val items = mutableListOf<MusicianUiModel>()

    fun submitList(newItems: List<MusicianUiModel>) {
        items.clear()
        items.addAll(newItems)
        try {
            notifyDataSetChanged()
        } catch (_: Exception) { /* ignore for unit tests */ }
    }

    var onMusicianClick: ((MusicianUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val binding = ItemMusicianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) : RecyclerView.ViewHolder(binding.root) {

        var loadJob: Job? = null

        fun bind(item: MusicianUiModel) {
            binding.txtName.text = item.name
            binding.txtSubtitle.text = item.subtitle

            loadJob?.cancel()

            loadJob = CoroutineScope(uiDispatcher).launch {
                if (!item.image.isNullOrEmpty()) {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            val url = URL(item.image)
                            BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        }
                        binding.imgArtist.setImageBitmap(bitmap)
                    } catch (e: CancellationException) {
                        // ignore
                    } catch (e: Exception) {
                        // ignore image load errors
                    }
                } else {
                    binding.imgArtist.setImageResource(R.drawable.vinyls_card_bg)
                }
            }

            binding.root.setOnClickListener { onMusicianClick?.invoke(item) }
        }
    }
}
