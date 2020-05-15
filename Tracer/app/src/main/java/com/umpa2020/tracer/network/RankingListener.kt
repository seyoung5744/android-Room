package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.InfoData

/**
 * 랭킹을 Database에서 다 가져왔오면 실행되는
 * Listener
 */
interface RankingListener {
  /**
   * 랭킹을 받아오면 infoDatas 를 반환
   */
  fun getRank(infoDatas: ArrayList<InfoData>, mode: String)
}