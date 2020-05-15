package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.PlayedMapData

/**
 * 내가 좋아요 누른 맵 목록 리스너
 */
interface PlayedMapListener {
  /**
   * 내가 좋아요 누른 맵 목록
   */
  fun played(playedMapDatas: ArrayList<PlayedMapData>)
}