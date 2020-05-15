package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.ActivityData

/**
 * 유저 히스토리 리스너
 */
interface ActivityListener {
  /**
   * 유저 히스토리 가져오기
   */
  fun activityList(activityData: ArrayList<ActivityData>)
}