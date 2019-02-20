package com.github.exact7.xtra.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.exact7.xtra.databinding.FragmentGamesBinding
import com.github.exact7.xtra.model.kraken.game.Game
import com.github.exact7.xtra.ui.common.BaseNetworkFragment
import com.github.exact7.xtra.ui.common.Scrollable
import com.github.exact7.xtra.ui.main.MainActivity
import kotlinx.android.synthetic.main.common_recycler_view_layout.view.*
import kotlinx.android.synthetic.main.fragment_games.*

class GamesFragment : BaseNetworkFragment(), Scrollable {

    interface OnGameSelectedListener {
        fun openGame(game: Game)
    }

    private lateinit var viewModel: GamesViewModel
    private lateinit var binding: FragmentGamesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            FragmentGamesBinding.inflate(inflater, container, false).let {
                binding = it
                it.setLifecycleOwner(viewLifecycleOwner)
                binding.root
            }

    override fun initialize() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GamesViewModel::class.java)
        binding.viewModel = viewModel
        val adapter = GamesAdapter(requireActivity() as MainActivity)
        recyclerViewLayout.recyclerView.adapter = adapter
        viewModel.list.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onNetworkRestored() {
        viewModel.retry()
    }

    override fun scrollToTop() {
        recyclerViewLayout.recyclerView.scrollToPosition(0)
    }
}
