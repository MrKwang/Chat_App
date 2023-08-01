package com.example.myapplication.apdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.fragments.ContactFragment
import com.example.myapplication.fragments.MessageFragment
import com.example.myapplication.fragments.SettingsFragment


class ViewPagerAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MessageFragment()
            1 -> ContactFragment()
            2 -> SettingsFragment()
            else -> MessageFragment()
        }
    }
}