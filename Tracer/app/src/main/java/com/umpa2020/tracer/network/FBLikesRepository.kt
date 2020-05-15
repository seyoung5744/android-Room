package com.umpa2020.tracer.network

import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.util.UserInfo

/**
 * 좋아요 관련 클래스
 * 사용법 - Likes().관련기능함수()
 */

class FBLikesRepository : BaseFB() {
  /**
   * 유저 인포에 저장되어있는 해당 유저가 좋아요 한 맵을 검사하여
   * 리사이클러뷰에 메시지를 보낸다.
   *
   * -> 리사이클러뷰에서 해당 유저가 좋아요한 맵에 대해서 이미지 작업을 한다.
   */
  fun listLikedMap(listener: LikedMapListener) {
    userInfoColRef.document(UserInfo.autoLoginKey).collection(USER_LIKED_THESE_MAPS)
      .get()
      .addOnSuccessListener { result ->
        listener.likedList(result.map {
          LikedMapData(
            it.getString(MAP_TITLE)
          )
        })
      }
  }

  fun getMapLike(mapTitle: String, listener: LikedMapListener) {
    userInfoColRef.document(UserInfo.autoLoginKey).collection(USER_LIKED_THESE_MAPS)
      .whereEqualTo(MAP_TITLE, mapTitle)
      .get()
      .addOnSuccessListener {
        mapInfoColRef.whereEqualTo(MAP_TITLE, mapTitle)
          .get()
          .addOnSuccessListener { result ->
            for (document in result) {
              val buffer = document.get(LIKES)
              // 사용자가 좋아한 맵에 현재 맵이 있으면 true 넘겨주고
              // 없으면 false 넘겨주기
              listener.liked(!it.isEmpty, Integer.parseInt(buffer.toString()))
            }
          }
      }
  }

  /**
   * 1. 유저가 좋아요 버튼을 눌렀을 때, 유저 인포에 좋아요한 맵을 저장하고
   * 2. 맵 인포에 유저 uid를 저장하고
   * 3. 맵 인포에 좋아요 숫자를 1 더한다
   */
  fun updateLikes(maptitle: String, likes: Int) {
    val likeMapsData = LikedMapData(maptitle)
    db.collection(USER_INFO).document(UserInfo.autoLoginKey).collection(USER_LIKED_THESE_MAPS)
      .add(likeMapsData)
    mapInfoColRef.document(maptitle).collection(LIKES).add(likeMapsData)
    mapInfoColRef.document(maptitle).update(LIKES, likes + 1)
  }

  /**
   * 1. 유저가 이미 좋아요한 맵을 취소할 때, 유저 인포에서 좋아요한 맵을 찾아 삭제하고
   * 2. 맵 인포에서 유저 uid를 삭제하고
   * 3. 맵 인포에서 좋아요 수를 1 감소한 값으로 업데이트
   */
  fun updateNotLikes(maptitle: String, likes: Int) {

    userInfoColRef.document(UserInfo.autoLoginKey).collection(USER_LIKED_THESE_MAPS)
      .whereEqualTo(MAP_TITLE, maptitle)
      .get()
      .addOnSuccessListener { result ->
        val documentid = result.documents[0].id
        userInfoColRef.document(UserInfo.autoLoginKey)
          .collection(USER_LIKED_THESE_MAPS)
          .document(documentid).delete()
      }

    mapInfoColRef.document(maptitle).collection(LIKES)
      .whereEqualTo(MAP_TITLE, maptitle)
      .get()
      .addOnSuccessListener { result ->
        val documentId = result.documents[0].id

        mapInfoColRef.document(maptitle).collection(LIKES)
          .document(documentId).delete()
      }
    mapInfoColRef.document(maptitle).update(LIKES, likes - 1)
  }
}