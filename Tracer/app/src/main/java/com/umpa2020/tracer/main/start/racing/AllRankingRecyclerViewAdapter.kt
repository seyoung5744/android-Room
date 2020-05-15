package com.umpa2020.tracer.main.start.racing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.*
import java.util.*

class AllRankingRecyclerViewAdapter(private val mydata: ArrayList<RankingData>) :
  RecyclerView.Adapter<AllRankingRecyclerViewAdapter.myViewHolder>() {
  var context: Context? = null

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: myViewHolder, position: Int) {
    val singleItem1 = mydata[position]
    val ranking = position + 1

    //데이터 바인딩
    holder.rank.text = ranking.toString()
    holder.nickname.text = singleItem1.challengerNickname
    holder.time.text = singleItem1.challengerTime!!.toLong().format(m_s)

    //ranking에 따라 트로피 색 바뀌게 하는 부분
    if (ranking == 1) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking1_black)
      holder.rank.text = ""
    } else if (ranking == 2) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking2_black)
      holder.rank.text = ""
    } else if (ranking == 3) {
      holder.rank.setBackgroundResource(R.drawable.ic_ranking3_black)
      holder.rank.text = ""
    } else
      holder.rank.setBackgroundResource(R.drawable.ic_4)

    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        val intent = Intent()
        intent.putExtra("result", holder.nickname.text.toString())
        App.instance.currentActivity()!!.setResult(100, intent)
        App.instance.currentActivity()!!.finish()
      }
    })

  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_rankfragment_topplayer_item, parent, false)
    context = parent.context
    return myViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반환
  override fun getItemCount(): Int {
    return mydata.size
  }

  //여기서 item을 textView에 옮겨줌

  inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var rank = view.rankRecyclerItemClickCountTextView
    var nickname = view.rankRecyclerItemClickChallengerNicknameTextView
    var time = view.rankRecyclerItemClickTimeTextView
  }
}

