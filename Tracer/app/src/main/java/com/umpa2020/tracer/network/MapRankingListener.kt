package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.RankingData

/**
 * 랭킹을 Database에서 다 가져왔오면 실행되는
 * Listener
 */
interface MapRankingListener {
  /**
   * 랭킹을 받아오면 infoDatas 를 반환
   */
  fun getMapRank(arrRankingData: ArrayList<RankingData>)
}