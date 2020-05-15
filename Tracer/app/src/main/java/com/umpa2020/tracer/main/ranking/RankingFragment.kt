package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.ANIMATION_DURATION_TIME
import com.umpa2020.tracer.constant.Constants.Companion.MAX_DISTANCE
import com.umpa2020.tracer.constant.Constants.Companion.MAX_SEEKERBAR
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.network.FBRankingRepository
import com.umpa2020.tracer.network.RankingListener
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment(), OnSingleClickListener {
  lateinit var location: LatLng
  lateinit var root: View
  lateinit var rankingRepo: FBRankingRepository

  var rootInfoDatas = arrayListOf<InfoData>()
  var distance = MAX_DISTANCE
  val progressbar = MyProgressBar()
  var tuneDistance = 0
  var isLoding = false
  var limit = 0L

  var rankingLatLng: LatLng? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Shared에서 마지막 위치 가져오기
    val latLng = LatLng(UserInfo.lat.toDouble(), UserInfo.lng.toDouble())
    rankingLatLng = latLng
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_ranking, container, false)
    root = view

    rankingRepo = FBRankingRepository(rankingListener)

    view.rank_recycler_map.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!rank_recycler_map.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!rank_recycler_map.canScrollVertically(1)) {
          // 리사이클러뷰가 맨 아래로 이동했을 경우
          if (requireView().tuneRadioBtnExecute.isChecked) {
            requireView().rankingfiltermode.text = getString(R.string.execute)
            if (!isLoding) {
              rankingRepo.listFilterRange(
                rankingLatLng!!,
                tuneDistance,
                "execute",
                20
              )
            }
          } else {
            requireView().rankingfiltermode.text = getString(R.string.likes)
            if (!isLoding) {
              rankingRepo.listFilterRange(
                rankingLatLng!!,
                tuneDistance,
                "likes",
                20
              )
            }
          }
          isLoding = true
        }
      }
    })

    view.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        distance = i

        view.progressTextView.text = distance.toString()
        if (i == MAX_SEEKERBAR) {
          view.progressTextView.text = "100+"
          distance = MAX_DISTANCE
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {}

      override fun onStopTrackingTouch(seekBar: SeekBar) {}
    })

    view.filterLayout.setOnClickListener(this)
    view.rankingToolBarTuneButton.setOnClickListener(this)
    view.disappearLayout.setOnClickListener(this)
    view.allDeleteButton.setOnClickListener(this)
    view.applyButton.setOnClickListener(this)

    return view
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.filterLayout -> { // 뒤 쪽 클릭 안되게 클릭 리스너 달아서 제한
      }
      R.id.rankingToolBarTuneButton -> { //필터 버튼 누르면 레이아웃 보임
        if (requireView().tuneLinearLayout.visibility == GONE) {
          appearAnimation()
        }
      }
      R.id.disappearLayout -> { //필터 레이아웃 밖 영역 클릭하면 사라짐
        disappearAnimation()
      }
      R.id.allDeleteButton -> { //기본 값 누를 때
        requireView().tuneRadioBtnExecute.isChecked = true
        requireView().tuneRadioBtnLike.isChecked = false
        requireView().progressTextView.text = "100+"
        requireView().seekBar.progress = MAX_DISTANCE
      }

      R.id.applyButton -> { //적용 버튼 누를때
        tuneDistance = distance
        progressbar.show()

        rootInfoDatas.clear()
        rankingRepo = FBRankingRepository(rankingListener)


        if (rankingLatLng != null) {
          //실행순 버튼에 체크가 되어 있을 경우
          if (requireView().tuneRadioBtnExecute.isChecked) {
            requireView().rankingfiltermode.text = getString(R.string.execute)

            rankingRepo.listRanking(
              rankingLatLng!!,
              tuneDistance,
              "execute",
              20
            )
          } else {
            requireView().rankingfiltermode.text = getString(R.string.likes)

            rankingRepo.listRanking(
              rankingLatLng!!,
              tuneDistance,
              "likes",
              20
            )
          }
          disappearAnimation()
        }
      }
    }
  }

  override fun onResume() {

    if (rankingLatLng != null) {
      tuneDistance = distance
      progressbar.show()
      limit = rootInfoDatas.size.toLong()

      if (limit == 0L) limit = 20L
      else rootInfoDatas.clear()

      rankingRepo = FBRankingRepository(rankingListener)

      if (rankingLatLng != null) {
        //실행순 버튼에 체크가 되어 있을 경우
        if (requireView().tuneRadioBtnExecute.isChecked) {
          requireView().rankingfiltermode.text = getString(R.string.execute)

          rankingRepo.listRanking(
            rankingLatLng!!,
            tuneDistance,
            "execute",
            limit
          )
        } else {
          requireView().rankingfiltermode.text = getString(R.string.likes)

          rankingRepo.listRanking(
            rankingLatLng!!,
            tuneDistance,
            "likes",
            limit
          )
        }
      }
    }
    super.onResume()
  }

  /**
   * 레이아웃이 스르륵 보이는 함수
   */

  private fun appearAnimation() {
    val animate = AlphaAnimation(0f, 1f) //투명도 변화
    animate.duration = ANIMATION_DURATION_TIME
    animate.fillAfter = true
    requireView().tuneLinearLayout.visibility = View.VISIBLE
    requireView().filterLayout.visibility = View.VISIBLE
    requireView().disappearLayout.visibility = View.VISIBLE
    requireView().tuneLinearLayout.startAnimation(animate)
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  private fun disappearAnimation() {
    requireView().tuneLinearLayout.visibility = View.GONE
    requireView().filterLayout.visibility = View.GONE
    requireView().disappearLayout.visibility = View.GONE

    val animate = AlphaAnimation(1f, 0f)
    animate.duration = ANIMATION_DURATION_TIME
    animate.fillAfter = true
    requireView().tuneLinearLayout.startAnimation(animate)
  }

  private val rankingListener = object : RankingListener {
    override fun getRank(infoDatas: ArrayList<InfoData>, mode: String) {
      rootInfoDatas.addAll(infoDatas)
      if (rootInfoDatas.isEmpty()) {
        rankingRecyclerRouteisEmpty.visibility = View.VISIBLE
        progressbar.dismiss()
      } else {
        //레이아웃 매니저, 어댑터 추가
        if (rootInfoDatas.size < 21) {
          rank_recycler_map.layoutManager = LinearLayoutManager(context)
          rank_recycler_map.adapter = MapRankingAdapter(rootInfoDatas, mode, progressbar)
        } else {
          rank_recycler_map.adapter!!.notifyDataSetChanged()
        }
        isLoding = false
      }
    }
  }

}
