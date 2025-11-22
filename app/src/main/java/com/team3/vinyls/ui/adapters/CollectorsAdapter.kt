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
import com.team3.vinyls.ui.models.CollectorUiModel
import com.team3.vinyls.ui.AvatarUtils

class CollectorsAdapter(
    private val uiDispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main,
    private val imageLoader: ImageLoader = GlideImageLoader()
) : ListAdapter<CollectorUiModel, CollectorsAdapter.CollectorViewHolder>(CollectorDiffCallback()) {

    var onCollectorClick: ((CollectorUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collector, parent, false)
        return CollectorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: CollectorViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class CollectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgCollector: ImageView = itemView.findViewById(R.id.imgCollector)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txtSubtitle)

        fun bind(item: CollectorUiModel) {
            txtName.text = item.name
            txtSubtitle.text = item.subtitle

            // If there's a remote image URL, use the ImageLoader; otherwise generate an initials avatar
            if (!item.image.isNullOrEmpty()) {
                imageLoader.load(item.image, imgCollector)
            } else {
                AvatarUtils.setInitialsAvatar(imgCollector, item.name, item.id)
            }

            itemView.setOnClickListener { onCollectorClick?.invoke(item) }
        }

        fun clear() {
            // Clear any image loading callbacks and set default placeholder via ImageLoader
            imageLoader.clear(imgCollector)
        }
    }

    class CollectorDiffCallback : DiffUtil.ItemCallback<CollectorUiModel>() {
        override fun areItemsTheSame(oldItem: CollectorUiModel, newItem: CollectorUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectorUiModel, newItem: CollectorUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
