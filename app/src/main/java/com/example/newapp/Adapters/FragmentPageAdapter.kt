package com.example.newapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newapp.fragments.*

class FragmentPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) generalNewsFragment()
        else if (position == 1) entertainmentNewsFragment()
        else if (position == 2) technologyNewsFragment()
        else if(position == 3) scienceNewsFragment()
        else if(position == 4) bussinessNewsFragment()
        else if(position == 5) sportsNewsFragment()
        else healthNewsFragment()
    }
}