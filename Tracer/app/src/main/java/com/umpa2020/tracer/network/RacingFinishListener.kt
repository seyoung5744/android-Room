package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.RankingData

/**
 * 레이싱이 끝났을 때,
 */
interface RacingFinishListener {
  /**
   * 랭킹을 받아오면 infoDatas 를 반환
   */
  fun getRacingFinish(rankingDatas: ArrayList<RankingData>, resultRank: Int)

  fun getOtherRacing(otherData: RankingData)
}