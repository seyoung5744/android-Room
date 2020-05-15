package com.umpa2020.tracer.main.start.racing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.extensions.toRank
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import java.util.*


class RacingFinishActivity : AppCompatActivity(), OnSingleClickListener {

  var activity = this
  lateinit var racerData: InfoData
  var arrRankingData: ArrayList<RankingData> = arrayListOf()
  lateinit var progressbar: ProgressBar

  var racerSpeeds = mutableListOf<Double>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()

    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    racerData = intent.getParcelableExtra("InfoData") as InfoData
    val result = intent.extras!!.getBoolean("Result")
    val racerGPX = intent.getParcelableExtra<RouteGPX>("RouteGPX")
    val mapRouteGPX = intent.getParcelableExtra<RouteGPX>("MapRouteGPX")
    racerSpeeds = racerGPX!!.getSpeed()

    // 유저 인포에 해당 유저가 이 맵을 뛰었다는
    // 히스토리를 더하는 함수
    FBRacingRepository().createUserInfoRacing(racerData)
    val timestamp = Date().time

    if (result) {
      val activityData =
        ActivityData(racerData.mapTitle, timestamp.toString(), "racing go the distance")
      FBUserActivityRepository().createUserHistory(activityData)
    } else {
      val activityData = ActivityData(racerData.mapTitle, timestamp.toString(), "racing fail")
      FBUserActivityRepository().createUserHistory(activityData)
    }


    FBRacingRepository().createRankingData(
      result,
      racerData,
      racingFinishListener,
      racerSpeeds,
      racerGPX
    )

    OKButton.setOnClickListener(this)
    otherPeopleProfileSelect.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      // 다 봤다는 표시 - 그래도 앞에 있던 액티비티들을 끄고, 메인 엑티비티 실행
      R.id.OKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }

      // 리스트 선택화면으로 넘어감
      R.id.otherPeopleProfileSelect -> {
        val intent = Intent(this, AllRankingActivity::class.java)
        intent.putExtra("arrRankingData", arrRankingData)
        startActivityForResult(intent, 100)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == 100) {
      if (resultCode == 100) {
        val getNickname = data!!.getStringExtra("result")
        RacingFinishAnalysisOtherNickname.text = getNickname

        progressbar.show()
        FBRacingRepository().getOtherData(racerData.mapTitle!!, getNickname!!, racingFinishListener)
        /**
         * TODO 아래 코드 원래 있던 코드를 재활용 안하고 새로 했는데 - 정빈
         *
         * 1. AllRankingActivity 안에 RecyclerView에서 종료를 시키고 있어서
         *    제대로 된 종료가 안되서
         * 2. 원래 있던 코드를 사용하면 APP.instance 부분이 오류가 나게 됨
         *
         */
        val db = FirebaseFirestore.getInstance()
        var profileImagePath = "init"
        db.collection("userinfo").whereEqualTo("nickname", getNickname)
          .get()
          .addOnSuccessListener { result ->
            if (!result.isEmpty) {
              for (document in result) {
                profileImagePath = document.get("profileImagePath") as String
                break
              }
              // glide imageview 소스
              // 프사 설정하는 코드 db -> imageView glide
              val storage = FirebaseStorage.getInstance()
              val profileRef = storage.reference.child(profileImagePath)

              profileRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                  // Glide 이용하여 이미지뷰에 로딩
                  Glide.with(App.instance.currentActivity() as Activity)
                    .load(task.result)
                    .override(1024, 980)
                    .into(RacingFinishAnalysisOtherProfile)
                  progressbar.dismiss()
                } else {
                  progressbar.dismiss()
                }
              }
            } else {
              RacingFinishAnalysisOtherNickname.text = "탈퇴한 회원입니다."
            }

          }
        //FBProfileRepository().getProfileImage(RacingFinishAnalysisOtherProfile, getNickname!!)
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun RouteGPX.getSpeed(): MutableList<Double> {
    val speeds = mutableListOf<Double>()
    trkList.forEach {
      speeds.add(it.speed!!)
    }
    return speeds
  }

  private fun setMyUiData(
    racerSpeeds: MutableList<Double>,
    resultRankText: Int
  ) {

    // 나의 기록
    FBProfileRepository().getProfileImage(racingFinishProfileImageView, UserInfo.nickname)
    RacingFinishMyNickName.text = UserInfo.nickname

    if (resultRankText == 0) {
      resultRankTextView.text = getString(R.string.fail)
    } else {
      resultRankTextView.text = resultRankText.toRank()
    }

    RacingFinishMyLapTime.text = racerData.time!!.format(m_s)

    FBProfileRepository().getProfileImage(RacingFinishAnalysisMyProfile, UserInfo.nickname)
    RacingFinishAnalysisMyNickname.text = UserInfo.nickname

    racerLapTimeTextView.text = racerData.time!!.format(m_s)
    racerMaxSpeedTextView.text = racerSpeeds.max()!!.prettyDistance
    racerAvgSpeedTextView.text = racerSpeeds.average().prettyDistance
    progressbar.dismiss()
  }

  private val racingFinishListener = object : RacingFinishListener {
    override fun getRacingFinish(rankingDatas: ArrayList<RankingData>, resultRank: Int) {
      arrRankingData = rankingDatas
      setMyUiData(racerSpeeds, resultRank)

      if (arrRankingData.size >= 1) {
        FBProfileRepository().getProfileImage(
          racingFinishProfileFirst,
          arrRankingData[0].challengerNickname!!
        )
        racingFinishNicknameFirst.text = arrRankingData[0].challengerNickname
        racingFinishLapTimeFirst.text = arrRankingData[0].challengerTime!!.format(m_s)

        FBProfileRepository().getProfileImage(
          RacingFinishAnalysisOtherProfile,
          arrRankingData[0].challengerNickname!!
        )

        RacingFinishAnalysisOtherNickname.text = arrRankingData[0].challengerNickname
        otherLapTimeTextView.text = arrRankingData[0].challengerTime!!.format(m_s)
        otherMaxSpeedTextView.text = arrRankingData[0].maxSpeed!!.toDouble().prettyDistance
        otherAvgSpeedTextView.text = arrRankingData[0].averageSpeed!!.toDouble().prettyDistance
      }

      if (arrRankingData.size >= 2) {
        FBProfileRepository().getProfileImage(
          racingFinishProfileSecond,
          arrRankingData[1].challengerNickname!!
        )
        racingFinishNicknameSecond.text = arrRankingData[1].challengerNickname
        racingFinishLapTimeSecond.text = arrRankingData[1].challengerTime!!.format(m_s)
      }

      if (arrRankingData.size >= 3) {
        FBProfileRepository().getProfileImage(
          racingFinishProfileThird,
          arrRankingData[2].challengerNickname!!
        )
        racingFinishNicknameThird.text = arrRankingData[2].challengerNickname
        racingFinishLapTimeThird.text = arrRankingData[2].challengerTime!!.format(m_s)
      }
    }

    override fun getOtherRacing(otherData: RankingData) {
      otherLapTimeTextView.text = otherData.challengerTime!!.format(m_s)
      otherMaxSpeedTextView.text = otherData.maxSpeed!!.toDouble().prettyDistance
      otherAvgSpeedTextView.text = otherData.averageSpeed!!.toDouble().prettyDistance
    }
  }
}
