package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikedMapData

/**
 * 레이싱 피니시에서 메이커 데이터를 받아오는 리스너
 */
interface GetMakerDataListener {
  /**
   * 메이커 데이터
   */
  fun makerData(getMakerData: InfoData)
}