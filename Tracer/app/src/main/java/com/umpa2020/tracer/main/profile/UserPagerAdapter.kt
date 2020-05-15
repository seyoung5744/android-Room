package com.umpa2020.tracer.main.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class UserPagerAdapter(fm: FragmentManager) :
  FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  class FragmentInfo(val iconOnResId: Int, val iconOffResId: Int, val titleText: String, val fragment: Fragment)

  private val mFragmentInfoList = ArrayList<FragmentInfo>()

  fun addFragment(iconOnResID: Int, iconOffResID: Int, title: String, fragment: Fragment) {
    val info = FragmentInfo(iconOnResID, iconOffResID, title, fragment)
    mFragmentInfoList.add(info)
  }

  override fun
    getItem(position: Int): Fragment {
    return mFragmentInfoList[position].fragment
  }

  override fun getCount(): Int {
    return mFragmentInfoList.size
  } // return 2

  override fun getPageTitle(position: Int): CharSequence {
    return mFragmentInfoList[position].titleText
  }
}
