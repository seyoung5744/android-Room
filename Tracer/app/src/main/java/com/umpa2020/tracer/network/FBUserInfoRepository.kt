package com.umpa2020.tracer.network

import com.umpa2020.tracer.util.Logg

class FBUserInfoRepository : BaseFB() {
  fun createUserInfo(data: HashMap<String, String?>) {
    db.collection(USER_INFO).document(data[UID]!!).set(data)
      .addOnSuccessListener { Logg.d("DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Logg.w("Error writing document$e") }
  }
}