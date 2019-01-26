package com.github.exact7.xtra.ui.streams

import androidx.recyclerview.widget.DiffUtil
import com.github.exact7.xtra.R
import com.github.exact7.xtra.databinding.FragmentStreamsListItemBinding
import com.github.exact7.xtra.model.kraken.stream.Stream
import com.github.exact7.xtra.ui.common.DataBoundPagedListAdapter
import com.github.exact7.xtra.ui.main.MainActivity

class StreamsAdapter(
        private val mainActivity: MainActivity) : DataBoundPagedListAdapter<Stream, FragmentStreamsListItemBinding>(
        object : DiffUtil.ItemCallback<Stream>() {
            override fun areItemsTheSame(oldItem: Stream, newItem: Stream): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Stream, newItem: Stream): Boolean =
                    oldItem.viewers == newItem.viewers &&
                            oldItem.game == newItem.game &&
                            oldItem.channel.status == newItem.channel.status
        }) {

    override val itemId: Int = R.layout.fragment_streams_list_item

    override fun bind(binding: FragmentStreamsListItemBinding, item: Stream?) {
        binding.stream = item
        binding.streamListener = mainActivity
        binding.channelListener = mainActivity
    }
}
