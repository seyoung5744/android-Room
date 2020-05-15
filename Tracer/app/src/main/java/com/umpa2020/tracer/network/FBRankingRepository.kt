package com.umpa2020.tracer.network

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.PlayedMapData
import com.umpa2020.tracer.network.BaseFB.Companion.EXECUTE
import com.umpa2020.tracer.network.BaseFB.Companion.LIKES
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_INFO


/**
 * 랭킹 네트워크 클래스 - 랭킹에 관련한
 * 네트워크 접근 함수는 이 곳에 정의
 */
class FBRankingRepository(rankingListener: RankingListener) {
  lateinit var infoData: InfoData
  lateinit var infoDatas: ArrayList<InfoData>
  val db = FirebaseFirestore.getInstance()
  lateinit var globalStartAfter: DocumentSnapshot

  /**
   * 현재 위치를 받아서 현재 위치와 필터에 적용한 위치 만큼 떨어져 있는 구간에서 실행순으로 정렬한 코드
   */
  fun listRanking(
    cur_loc: LatLng,
    distance: Int,
    mode: String,
    limit: Long
  ) {
    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current

    db.collection(MAP_INFO)
      .orderBy(mode, Query.Direction.DESCENDING)
      .limit(limit)
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          infoData = document.toObject(InfoData::class.java)
          infoData.mapTitle = document.id
          infoData.distance = SphericalUtil.computeDistanceBetween(
            cur_loc,
            LatLng(infoData.startLatitude!!, infoData.startLongitude!!)
          )
          if (infoData.distance!! < distance * 1000) {
            infoDatas.add(infoData)
          }
          globalStartAfter = document
        }
        if (mode == EXECUTE) {
          FBPlayedRepository().listPlayed(playedMapListener)
        } else if (mode == LIKES) {
          FBLikesRepository().listLikedMap(likedMapListener)
        }
      }
  }

  fun listFilterRange(
    cur_loc: LatLng,
    distance: Int,
    mode: String,
    limit: Long
  ) {
    //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
    //Location 형태로 받아오고 싶다면 아래처럼
    //var getintentLocation = current

    db.collection(MAP_INFO)
      .orderBy(mode, Query.Direction.DESCENDING)
      .startAfter(globalStartAfter)
      .limit(limit)
      .get()
      .addOnSuccessListener { result ->
        infoDatas = arrayListOf()

        for (document in result) {
          infoData = document.toObject(InfoData::class.java)
          infoData.mapTitle = document.id
          infoData.distance = SphericalUtil.computeDistanceBetween(
            cur_loc,
            LatLng(infoData.startLatitude!!, infoData.startLongitude!!)
          )
          if (infoData.distance!! < distance * 1000) {
            infoDatas.add(infoData)
          }
          globalStartAfter = document
        }
        if (mode == EXECUTE) {
          FBPlayedRepository().listPlayed(playedMapListener)
        } else if (mode == LIKES) {
          FBLikesRepository().listLikedMap(likedMapListener)
        }
      }
  }

  private val playedMapListener = object : PlayedMapListener {
    override fun played(playedMapDatas: ArrayList<PlayedMapData>) {
      infoDatas.filter { infoData ->
        playedMapDatas.map { it.mapTitle }
          .contains(infoData.mapTitle)
      }.map { it.played = true }

      rankingListener.getRank(infoDatas, EXECUTE)
    }
  }

  // 좋아요 필터를 눌렀을 때, 유저가 좋아요 누른 맵들을 가져오는 리스너
  private val likedMapListener = object : LikedMapListener {
    override fun likedList(likedMaps: List<LikedMapData>) {
      infoDatas.filter { infoData ->
        likedMaps.map { it.mapTitle }
          .contains(infoData.mapTitle)
      }.map { it.myLiked = true }
      rankingListener.getRank(infoDatas, LIKES)
    }

    override fun liked(liked: Boolean, likes: Int) {
    }
  }
}