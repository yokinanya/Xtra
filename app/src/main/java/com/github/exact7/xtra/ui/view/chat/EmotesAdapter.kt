package com.github.exact7.xtra.ui.view.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.chat.Emote
import com.github.exact7.xtra.util.loadBitmap
import com.github.exact7.xtra.util.loadImage

class EmotesAdapter(
        private val clickListener: (Emote) -> Unit,
        private val animateGifs: Boolean) : ListAdapter<Emote, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Emote>() {
    override fun areItemsTheSame(oldItem: Emote, newItem: Emote): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Emote, newItem: Emote): Boolean {
        return true
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_emotes_list_item, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val emote = getItem(position)
        (holder.itemView as ImageView).apply {
            if (animateGifs) {
                loadImage(emote.url)
            } else {
                loadBitmap(emote.url)
            }
            setOnClickListener { clickListener(emote) }
        }
    }
}