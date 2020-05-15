package com.umpa2020.tracer.network

/**
 * 프로필 관련 리스너
 */
interface ProfileListener {
  /**
   * 뛴 거리, 뛴 시간 가져오기
   */
  fun getProfile(distance: Double, time: Double)

  /**
   *
   */
  fun changeProfile()
}