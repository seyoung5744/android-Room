package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.dataClass.LikedMapData

/**
 * 챌린지 데이터 리스너
 */
interface ChallengeDataListener {
  /**
   * 챌린지 데이터 리스트 받아오는 함수
   */
  fun challengeDataList(listChallengeData: MutableList<ChallengeData>) {
  }


  /**
   * 챌린지 데이터 하나만 받아오는 함수
   */
  fun challengeData(challengeData: ChallengeData) {

  }


}