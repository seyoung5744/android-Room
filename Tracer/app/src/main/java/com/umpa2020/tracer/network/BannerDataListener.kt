package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.BannerData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData

/**
 * 배너 데이터 리스너
 */
interface BannerDataListener {
  /**
   * 배너 이미지 path, id 리스트 받아오는 함수
   */
  fun bannerDataList(listBannerData: ArrayList<BannerData>) {
  }

}