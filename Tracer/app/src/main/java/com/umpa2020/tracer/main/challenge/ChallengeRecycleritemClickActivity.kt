package com.umpa2020.tracer.main.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.network.ChallengeDataListener
import com.umpa2020.tracer.network.FBChallengeImageRepository
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_challenge_map_detail.*

class ChallengeRecycleritemClickActivity : AppCompatActivity(), OnSingleClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_map_detail)

    val challengeId = intent.getStringExtra("challengeId")!!

    FBChallengeRepository().getChallengeData(challengeId, challengeDataListener)
  }

  override fun onSingleClick(v: View?) {
  }

  val challengeDataListener = object : ChallengeDataListener {
    @SuppressLint("SetTextI18n")
    override fun challengeData(challengeData: ChallengeData) {
      FBChallengeImageRepository().getChallengeImage(challengeDetailImageView, challengeData.imagePath!!)

      challengeDetailCompetitionName.text = challengeData.name
      challengeDetailCompetitionDate.text = challengeData.date!!.format(Y_M_D)
      challengeDetailCompetitionPeriod.text = "${challengeData.from!!.format(Y_M_D)} ~ ${challengeData.to!!.format(Y_M_D)}"
      challengeDetailAddress.text = challengeData.address
      challengeDetailHost.text = challengeData.host
      challengeDetailInformation.text = challengeData.intro

    }
  }
}

