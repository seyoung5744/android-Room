package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.network.BaseFB.Companion.UID
import com.umpa2020.tracer.network.BaseFB.Companion.USER_ACTIVITY
import com.umpa2020.tracer.network.BaseFB.Companion.USER_INFO
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo

/**
 * 유저 히스토리 관련 클래스
 * 사용법 - FBUserHistoryRepository().관련기능함수()
 *
 * 1. 유저가 뛴 맵 history 저장
 * 2. 유저가 만든 맵 history 저장
 * 3. 유저가 challenge 한 맵 history 저장
 */

class FBUserActivityRepository {
  val db = FirebaseFirestore.getInstance()
  lateinit var globalStartAfter: DocumentSnapshot


  fun createUserHistory(activityData: ActivityData) {
    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(USER_ACTIVITY).add(activityData)
      }
  }

  fun listUserMakingActivityFirst(activityListener: ActivityListener, limit: Long) {

    Logg.d("ssmm11 limit = $limit")
    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection(USER_ACTIVITY)
          .limit(limit)
          .get()
          .addOnSuccessListener {
            activityListener.activityList(ArrayList(it.documents.map {
              globalStartAfter = it
              it.toObject(ActivityData::class.java)!!
            }))
          }
      }
  }

  fun listUserMakingActivity(activityListener: ActivityListener, limit: Long) {

    db.collection(USER_INFO).whereEqualTo(UID, UserInfo.autoLoginKey)
      .get()
      .addOnSuccessListener { it ->
        it.documents.last().reference.collection(USER_ACTIVITY)
          .startAfter(globalStartAfter)
          .limit(limit)
          .get()
          .addOnSuccessListener {
            activityListener.activityList(ArrayList(it.documents.map {
              globalStartAfter = it
              it.toObject(ActivityData::class.java)!!
            }))
          }
      }
  }
}