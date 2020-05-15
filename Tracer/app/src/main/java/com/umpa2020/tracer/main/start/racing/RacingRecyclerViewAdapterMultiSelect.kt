package com.umpa2020.tracer.main.start.racing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.show
import com.umpa2020.tracer.extensions.toRank
import com.umpa2020.tracer.main.profile.OtherProfileActivity
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo.nickname
import kotlinx.android.synthetic.main.recycler_racing_select_people_item.view.*
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.*
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.rankRecyclerItemClickChallengerNicknameTextView
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.rankRecyclerItemClickCountTextView
import kotlinx.android.synthetic.main.recycler_rankfragment_topplayer_item.view.rankRecyclerItemClickTimeTextView
import java.util.*

class RacingRecyclerViewAdapterMultiSelect(
  val mydata: ArrayList<RankingData>,
  val mapTitle: String,
  val tagContainerLayout: TagContainerLayout
) : RecyclerView.Adapter<RacingRecyclerViewAdapterMultiSelect.myViewHolder>() {
  var context: Context? = null
  val whichIsCheck = mutableListOf<String>()
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


    if (whichIsCheck.contains(singleItem1.challengerNickname)) {
      //checked
      holder.checked.visibility = View.VISIBLE
    } else {
      //unchecked
      holder.checked.visibility = View.INVISIBLE
    }

    //클릭했을 때
    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        //다시 선택하면 취소
        if (whichIsCheck.contains(singleItem1.challengerNickname)) {
          whichIsCheck.remove(singleItem1.challengerNickname)
          tagContainerLayout.tags.forEachIndexed {i,text->
            if(text == singleItem1.challengerNickname){
              tagContainerLayout.removeTag(i)
            }
            notifyDataSetChanged()
          }
        }
        else{
          //size 개수 제한
          if(whichIsCheck.size < 6){
            //리스트 뷰에서 선택한 닉네임을 태그 뷰로 추가
            whichIsCheck.add(singleItem1.challengerNickname!!)
            tagContainerLayout.addTag(singleItem1.challengerNickname)
            notifyDataSetChanged()
          }
          else
           context!!.getString(R.string.max_five).show()
        }
      }
    })

  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_racing_select_people_item, parent, false)
    context = parent.context
    Logg.d("onCreateViewHolder호출")
    return myViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반환
  override fun getItemCount(): Int {
    Logg.d("데이터 크기 " + mydata.size.toString())
    return mydata.size
  }

  //여기서 item을 textView에 옮겨줌

  inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var rank = view.rankRecyclerItemClickCountTextView
    var nickname = view.rankRecyclerItemClickChallengerNicknameTextView
    var time = view.rankRecyclerItemClickTimeTextView
    var checked = view.selectPeopleCheckImage
  }


}

