package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.InfoData

/**
 * 프로필 루트 관련 리스너
 */
interface ProfileRouteListener {
  /**
   * 프로필 루트 받아오면 전달
   */
  fun listProfileRoute(infoDatas: ArrayList<InfoData>)


}