package com.umpa2020.tracer.network

import android.net.Uri
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RanMapsData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.classToGpx
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import java.io.File
import java.util.*

class FBRacingRepository : BaseFB() {

  /**
   * 첫 번째 실행 되어야하는 함수
   * 현재 유저와 다른 레이서들의 기록을 비교하여
   * 최고 기록일 경우 bestTime을 1로 설정하고
   * 최고 기록이 아닐 경우 bestTime을 0으로 설정하여
   * ranking 에 등록하는 함수
   */
  fun createRankingData(
    result: Boolean,
    racerData: InfoData,
    racingFinishListener: RacingFinishListener,
    racerSpeeds: MutableList<Double>,
    racerGPX: RouteGPX
  ) {
    // 타임스탬프
    val timestamp = Date().time

    if (result) {
      val rankingData = RankingData(
        racerData.makersNickname,
        UserInfo.autoLoginKey,
        UserInfo.nickname,
        racerData.time,
        true,
        racerSpeeds.max().toString(),
        racerSpeeds.average().toString(),
        null
      )
      // 랭킹 맵에서
      db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
        .whereEqualTo(BEST_TIME, true)
        .get()
        .addOnSuccessListener {
          for (document in it) {
            if (document.get(CHALLENGER_NICKNAME) == UserInfo.nickname) {
              if (racerData.time!!.toLong() < document.get(CHALLENGER_TIME) as Long) {
                db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
                  .document(document.id).update(BEST_TIME, false)

                val saveFolder = File(App.instance.filesDir, "routeGPX") // 저장 경로
                if (!saveFolder.exists()) {       //폴더 없으면 생성
                  saveFolder.mkdir()
                }

                val racerGpxFile = racerGPX.classToGpx(saveFolder.path)

                // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
                val fstorage = FirebaseStorage.getInstance()
                val fRef = fstorage.reference.child(MAP_ROUTE)
                  .child(racerData.mapTitle!!).child(RACING_GPX).child(UserInfo.autoLoginKey)
                rankingData.racerGPX = fRef.path

                val fuploadTask = fRef.putFile(racerGpxFile)

                fuploadTask.addOnFailureListener {
                  Logg.d("Success to upload racerGPX")
                }.addOnSuccessListener {
                  Logg.d("Fail : $it")
                }
              } else {
                rankingData.bestTime = false
              }
            }
          }
          db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
            .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

          getRacingFinishRank(result, racerData, racingFinishListener)
        }
    } else {
      getRacingFinishRank(result, racerData, racingFinishListener)
    }
  }

  /**
   * 랭킹맵에서 올라와 있는 기록들을
   * 1. 동일한 유저의 기록은 최고 기록만 저장
   * 2. 성공했을 때에만 N등
   * 3. 실패했을 경우 실패
   */
  private fun getRacingFinishRank(
    result: Boolean,
    racerData: InfoData,
    racingFinishListener: RacingFinishListener
  ) {
    db.collection(RANKING_MAP).document(racerData.mapTitle!!).collection(RANKING)
      .orderBy(CHALLENGER_TIME, Query.Direction.ASCENDING)
      .whereEqualTo(BEST_TIME, true)
      .get()
      .addOnSuccessListener { resultdb ->
        var index = 1
        val arrRankingData = resultdb.map {
          val rankData: RankingData = it.toObject(RankingData::class.java)
          if (racerData.time!! >= rankData.challengerTime!!) {
            index++
          }
          rankData
        }
        racingFinishListener.getRacingFinish(ArrayList(arrRankingData), index)
      }
  }

  /**
   * 메이커 인포데이터를 가져오는 함수
   */
  fun getMakerData(racerData: InfoData, getMakerDataListener: GetMakerDataListener) {
    lateinit var makerData: InfoData

    db.collection(MAP_INFO).document(racerData.mapTitle!!)
      .get()
      .addOnSuccessListener { document ->
        makerData = document.toObject(InfoData::class.java)!!
        getMakerDataListener.makerData(makerData)
      }
  }

  /**
   * 다른 사람 인포데이터를 가져오는 함수
   */
  fun getOtherData(mapTitle: String, nickname: String, racingFinishListener: RacingFinishListener) {

    db.collection(RANKING_MAP).document(mapTitle)
      .collection(RANKING).whereEqualTo(CHALLENGER_NICKNAME, nickname)
      .whereEqualTo(BEST_TIME, true)
      .get()
      .addOnSuccessListener {
        val rankingData = it.documents.last().toObject(RankingData::class.java)
        racingFinishListener.getOtherRacing(rankingData!!)
      }
  }

  /**
   * 유저 인포에 해당 유저가 이 맵을 뛰었다는
   * 히스토리를 더하는 함수
   */
  fun createUserInfoRacing(racerData: InfoData) {
    val ranMapsData = RanMapsData(racerData.mapTitle, racerData.distance, racerData.time)
    db.collection(USER_INFO).document(UserInfo.autoLoginKey).collection(USER_RAN_THESE_MAPS)
      .add(ranMapsData)
  }

  fun listRacingGPX(mapTitle: String, racerIdList: Array<String>, listener: RacingListener) {
    val racerGPXList = mutableListOf<RouteGPX>()
    var count = 0
    racerIdList.forEach {
      val routeRef = mapRouteStorageRef.child(mapTitle).child(RACING_GPX).child(it)
      val localFile = File.createTempFile("routeGpx", "xml")
      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        racerGPXList.add(localFile.path.gpxToClass())
        count++
        if (racerGPXList.size == racerIdList.size) {
          listener.racingList(racerGPXList.toTypedArray())
        }
      }
    }
  }
}