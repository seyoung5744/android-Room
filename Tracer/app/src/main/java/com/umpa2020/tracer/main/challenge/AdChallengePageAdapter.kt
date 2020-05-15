package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.demono.adapter.InfinitePagerAdapter
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.AdChallengeData
import com.umpa2020.tracer.dataClass.BannerData
import com.umpa2020.tracer.network.FBChallengeImageRepository
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.fragment_adchallenge.view.*

class AdChallengePageAdapter(
  val adChallengeList: ArrayList<BannerData>,
  val context: Context
) : InfinitePagerAdapter() {
  override fun getItemCount(): Int {
    return adChallengeList.size
  }

  override fun getItemView(position: Int, convertView: View?, container: ViewGroup?): View {
    val view = LayoutInflater.from(container!!.context)
      .inflate(R.layout.fragment_adchallenge, container, false)

    FBChallengeImageRepository().getChallengeImage(
      view.adChallengeImgView,
      adChallengeList[position].bannerImagePath!!
    )
    view.setOnClickListener {
      if (position != 0) { // 코로나 안내이면 안 넘기도록 추후에 else 해서 자세한 안내 띄우도록
        val intent = Intent(context, ChallengeRecycleritemClickActivity::class.java)
        intent.putExtra("challengeId", adChallengeList[position].bannerId)
        context.startActivity(intent)
      }
    }
    return view
  }

}