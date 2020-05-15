package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.PlayedMapData
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_TITLE
import com.umpa2020.tracer.network.BaseFB.Companion.UID
import com.umpa2020.tracer.network.BaseFB.Companion.USER_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.USER_RAN_THESE_MAPS
import com.umpa2020.tracer.util.UserInfo

/**
 * 내가 뛰었던 맵 가져오는 Repository
 */

class FBPlayedRepository {
  val db = FirebaseFirestore.getInstance()
  val playedMapDatas = arrayListOf<PlayedMapData>()

  fun listPlayed(playedMapListener: PlayedMapListener) {
    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.last().reference.collection(USER_RAN_THESE_MAPS)
          .get().addOnSuccessListener { documents ->
            documents.forEach {
              val playedMapData = PlayedMapData(it.get(MAP_TITLE) as String, UserInfo.autoLoginKey)
              playedMapDatas.add(playedMapData)
            }
            playedMapListener.played(playedMapDatas)
          }
      }
  }
}