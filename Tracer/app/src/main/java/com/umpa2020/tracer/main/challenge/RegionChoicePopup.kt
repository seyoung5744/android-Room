package com.umpa2020.tracer.main.challenge

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.challenge_loaclpopup.*

class RegionChoicePopup(context: Context, val onClickListener: View.OnClickListener) :
  AlertDialog(context) {

  val localeList = arrayOf(
    "전국",
    "서울",
    "경기",
    "강원",
    "충북",
    "충남",
    "전남",
    "전북",
    "경북",
    "경남",
    "부산",
    "대구",
    "인천",
    "광주",
    "대전",
    "울산",
    "세종",
    "제주"
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.challenge_loaclpopup)
    window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


    challenge_local.layoutManager = GridLayoutManager(context, 3)
    challenge_local.adapter = LocalAdapter(localeList, onClickListener)
  }
}
