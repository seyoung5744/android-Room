package com.umpa2020.tracer.network

import android.os.Handler
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.network.BaseFB.Companion.DISTANCE
import com.umpa2020.tracer.network.BaseFB.Companion.EXECUTE
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_INFO
import com.umpa2020.tracer.network.BaseFB.Companion.START_LATITUDE
import com.umpa2020.tracer.network.BaseFB.Companion.START_LONGITUDE
import com.umpa2020.tracer.util.Logg

class FBMapRepository {
  val NEARMAPFALSE = 41
  val STRAT_FRAGMENT_NEARMAP = 30
  val db = FirebaseFirestore.getInstance()
  private var nearMaps: ArrayList<NearMap> = arrayListOf()

  fun listNearMap(southwest: LatLng, northeast: LatLng, mHandler: Handler) {
    // 경도선에 걸린 좌표 값

    db.collection(MAP_INFO)
      .whereGreaterThan(START_LATITUDE, southwest.latitude)
      .whereLessThan(START_LATITUDE, northeast.latitude)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val startLongitude = document.get(START_LONGITUDE) as Double
          val startLatitude = document.get(START_LATITUDE) as Double
          if (southwest.longitude > 0 && northeast.longitude < 0) {
            if (southwest.longitude < startLongitude || startLongitude < northeast.longitude) {
              val nearMap = NearMap(
                document.id,
                LatLng(startLatitude, startLongitude),
                document.get(DISTANCE) as Double
              )
              nearMaps.add(nearMap)
            }
          } else if (southwest.longitude < startLongitude && startLongitude < northeast.longitude) {
            val nearMap = NearMap(
              document.id,
              LatLng(startLatitude, startLongitude),
              document.get(DISTANCE) as Double
            )
            nearMaps.add(nearMap)
          }
        }
        val msg: Message = if (nearMaps.isEmpty()) {
          mHandler.obtainMessage(NEARMAPFALSE)
        } else {
          mHandler.obtainMessage(STRAT_FRAGMENT_NEARMAP)
        }
        msg.obj = nearMaps
        mHandler.sendMessage(msg)

      }
  }

  fun updateExecute(mapTitle: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection(MAP_INFO).document(mapTitle)
      .update(EXECUTE, FieldValue.increment(1))
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully updated!") }
      .addOnFailureListener { e -> Logg.w("Error updating document$e") }
  }
}