package com.umpa2020.tracer.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.main.profile.myroute.ProfileRouteActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.ProfileListener
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_other_profile.*
import org.jetbrains.anko.contentView

class OtherProfileActivity : AppCompatActivity(), OnSingleClickListener {
  var nickname = ""
  val progressBar = MyProgressBar()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_other_profile)
    progressBar.show()

    val intent = intent
    //전달 받은 값으로 Title 설정
    val mapTitle = intent.extras?.getString("mapTitle").toString()
    nickname = intent.extras?.getString("nickname").toString()

    // 넘어온 닉네임으로 현재 액티비티 닉네임 적용
    profileIdTextView.text = nickname
    //TODO : 테스트용 주석, 주석 해제하세요
    //FBProfileRepository().getProfile(contentView!!, nickname, profileListener)
    otherProfileRouteTextView.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when(v!!.id){
      R.id.otherProfileRouteTextView->{
        // 루트 클릭하면 해당 사용자가 만든 루트 볼 수 잇는 페이지로 이동
        val nextIntent = Intent(this, ProfileRouteActivity::class.java)
        nextIntent.putExtra("nickname", nickname)
        startActivity(nextIntent)
      }
    }
  }

  private val profileListener = object : ProfileListener {
    override fun getProfile(distance: Double, time: Double) {
      // 총 거리와 시간을 띄워줌
      profileFragmentTotalDistance.text = distance.prettyDistance
      profileFragmentTotalTime.text = time.toLong().format(m_s)
    }

    override fun changeProfile() {

    }
  }

}
