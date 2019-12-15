package com.example.movies.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.fragments.ViewPagerFragment
import com.example.movies.models.PopularModel

class ViewPagerAdapter(var fragmentManager: FragmentManager, var list: MutableList<BaseItem>) :
    FragmentPagerAdapter(fragmentManager) {


    override fun getItem(position: Int): Fragment {
        return ViewPagerFragment.newInstance(list[position] as PopularModel )
    }

    override fun getCount(): Int {
        return 5
    }



}