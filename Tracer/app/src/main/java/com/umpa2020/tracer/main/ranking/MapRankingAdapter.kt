package com.umpa2020.tracer.main.ranking

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.network.FBLikesRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_rankfragment_item.view.*

/**
 * 맵 랭킹 목록 어댑터
 */
class MapRankingAdapter(
  val infoDatas: ArrayList<InfoData>,
  val mode: String,
  val progressBar: MyProgressBar
) : RecyclerView.Adapter<MapRankingAdapter.mViewHolder>() {

  var context: Context? = null

  //  var holder1 : mViewHolder? = null
  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(
    holder: mViewHolder,
    position: Int
  ) {
    val infoData = infoDatas[position]
    val ranking = position + 1


    val cutted = infoData.mapTitle!!.subSequence(0, infoData.mapTitle!!.length - TIMESTAMP_LENGTH)

    //데이터 바인딩
    holder.rank.text = ranking.toString()

    holder.maptitle.text = cutted
    holder.distance.text = infoData.distance!!.prettyDistance
    if (mode.equals("execute")) {
      holder.modeIcon.setImageResource(R.drawable.ic_sneaker_for_running)
      holder.modeIcon.tag = R.drawable.ic_sneaker_for_running

      if (infoData.played) {
        holder.modeIcon.setColorFilter(R.color.green)
      }
      holder.modeNo.text = infoData.execute.toString()
    } else if (mode.equals("likes")) {
      if (infoData.myLiked) {
        holder.modeIcon.setImageResource(R.drawable.ic_favorite_red_24dp)
        holder.modeIcon.tag = R.drawable.ic_favorite_red_24dp
      } else {
        holder.modeIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        holder.modeIcon.tag = R.drawable.ic_favorite_border_black_24dp
      }
      holder.modeNo.text = infoData.likes.toString()
    }

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


    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(
      object : OnSingleClickListener {
        override fun onSingleClick(v: View?) {
          val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
          nextIntent.putExtra("MapTitle", infoData.mapTitle) //mapTitle 정보 인텐트로 넘김
          context!!.startActivity(nextIntent)
        }
      }
    )

    holder.modeIcon.setOnClickListener(
      object : OnSingleClickListener {
        override fun onSingleClick(v: View?) {
          var likes = Integer.parseInt(holder.modeNo.text.toString())

          when (holder.modeIcon.tag) {
            R.drawable.ic_sneaker_for_running -> {

            }
            R.drawable.ic_favorite_border_black_24dp -> {
              FBLikesRepository().updateLikes(infoData.mapTitle!!, likes)
              infoDatas[position].myLiked = true
              infoDatas[position].likes = ++likes
              holder.modeIcon.setImageResource(R.drawable.ic_favorite_red_24dp)
              holder.modeIcon.tag = R.drawable.ic_favorite_red_24dp
              holder.modeNo.text = likes.toString()
            }
            R.drawable.ic_favorite_red_24dp -> {
              FBLikesRepository().updateNotLikes(infoData.mapTitle!!, likes)
              infoDatas[position].myLiked = false
              infoDatas[position].likes = --likes
              holder.modeIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
              holder.modeIcon.tag = R.drawable.ic_favorite_border_black_24dp
              holder.modeNo.text = likes.toString()
            }
          }
        }
      })

    // 정보를 다 표현하면 dismiss
    // > 5를 한 이유는 recyclerview 특성 상 모든 정보를 한 번에 담는게 아니라
    // 스크롤이 내려가면 달게 posiotion이 증가 되어서 mdata.size 까지
    // 도달하지 못하는 경우가 있음
    // 추후에 코드 정리 할 예정 - 정빈
    if (position == infoDatas.size - 1 || position > 5) {
      if (progressBar.mprogressBar.isShowing) {
        progressBar.dismiss()
      }
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): mViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_rankfragment_item, parent, false)
    context = parent.context
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    //return 10
    return infoDatas.size
  }

  //여기서 item을 textView에 옮겨줌
  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var rank = view.rankingFragmentCountTextView
    var maptitle = view.rankingFragmentMapTitleTextView
    var distance = view.rankingFragmentDistanceTextView
    var modeNo = view.rankingFragmentModeTextView
    var modeIcon = view.rankingFragmentModeImageView
  }
}

