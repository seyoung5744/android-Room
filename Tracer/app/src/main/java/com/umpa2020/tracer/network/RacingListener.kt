package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteGPX

interface RacingListener {
  /**
   * 랭킹을 받아오면 infoDatas 를 반환
   */
  fun racingList(racerGPXList:Array<RouteGPX>)
}