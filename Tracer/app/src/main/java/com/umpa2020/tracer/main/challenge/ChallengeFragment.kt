package com.umpa2020.tracer.main.challenge

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.AdChallengeData
import com.umpa2020.tracer.dataClass.BannerData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.M_D
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.mm_ss
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChallengeFragment : Fragment() {
  var adChallengeList = ArrayList<BannerData>()

  var from = 1546300800000 // 경기 시작일 기본 값 (2019년 1월 1일)
  var to = 1609372800000 // 경기 종료일 기본 값 (2020년 12월 31일)
  var locale = "전국" // 필터링 지역 기본 값

  lateinit var regionChoicePopup: RegionChoicePopup // 지역 선택했을 때 팝업

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)
    val now = Calendar.getInstance()

    view.btn_challenge_from.text = from.format(Y_M_D)
    view.btn_challenge_to.text = to.format(Y_M_D)
    view.btn_challenge_region.text = locale

    FBChallengeBannerRepository().listChallengeBannerImagePath(bannerDataListener)
    FBChallengeRepository().listChallengeData(from, to, "전국", challengeDataListener)

    view.challengeAppBarText.setOnClickListener {
      val intent = Intent(context, ChallengeDataSettingActivity::class.java)
      startActivity(intent)
    }

    val a = view.adChallengeScrollViewPager.currentItem
    view.adChallengeScrollViewPager.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      /*adChallengeCountTextView.text =
        "${(view.adChallengeScrollViewPager.currentItem - a) % adChallengeList.size + 1}/${adChallengeList.size}"*/
    }

    /**
     * 경기 시작일 날짜 시작 클릭 리스너
     */
    view.btn_challenge_from.setOnClickListener {
      val datePicker = DatePickerDialog(
        requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
          val selectedDateFrom = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
          }.timeInMillis
          btn_challenge_from.text = selectedDateFrom.format(M_D)
          from = selectedDateFrom
        },
        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
      )
      datePicker.show()
    }

    /**
     * 경기 종료일 날짜 시작 클릭 리스너
     */
    view.btn_challenge_to.setOnClickListener {
      val datePicker = DatePickerDialog(
        requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
          val selectedDateTo = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
          }.timeInMillis
          btn_challenge_to.text = selectedDateTo.format(M_D)
          to = selectedDateTo
        },
        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
      )
      datePicker.show()
    }

    /**
     * 경기 지역 클릭 리스너
     */
    view.btn_challenge_region.setOnClickListener {
      regionChoicePopup = RegionChoicePopup(requireContext(), View.OnClickListener { localeButton ->
        locale = (localeButton as Button).text.toString()
        btn_challenge_region.text = locale
        regionChoicePopup.dismiss()
      })
      regionChoicePopup.show()
    }

    view.btn_challenge_search.setOnClickListener {
      FBChallengeRepository().listChallengeData(from, to, locale, challengeDataListener)
    }
    return view
  }

  /**
   * 배너에 들어가는 이미지와 아이디를 가져오는 리스너
   */
  private val bannerDataListener = object : BannerDataListener {
    override fun bannerDataList(listBannerData: ArrayList<BannerData>) {
      adChallengeList = listBannerData
      adChallengeScrollViewPager.adapter = AdChallengePageAdapter(adChallengeList, requireContext())
      adChallengeScrollViewPager.startAutoScroll()
    }
  }

  /**
   * 챌린지 프래그먼트 리사이클러 등록하는 리스너
   */
  private val challengeDataListener = object : ChallengeDataListener {
    override fun challengeDataList(listChallengeData: MutableList<ChallengeData>) {
      challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(listChallengeData)
      challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
    }
  }
}


