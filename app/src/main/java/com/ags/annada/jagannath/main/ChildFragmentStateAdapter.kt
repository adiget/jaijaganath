package com.ags.annada.jagannath.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ags.annada.jagannath.darshan.DarshanFragment
import com.ags.annada.jagannath.datasource.network.api.Contracts
import com.ags.annada.jagannath.home.PlayListFragment
import com.ags.annada.jagannath.mangalaarti.MangalaAratiFragment

class ChildFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MangalaAratiFragment.newInstance(Contracts.PLAYLIST_ID)
            1 -> PlayListFragment.newInstance(Contracts.CHANNEL_ID)
            else -> DarshanFragment.newInstance(Contracts.PLAYLIST_ID)
        }
    }
}