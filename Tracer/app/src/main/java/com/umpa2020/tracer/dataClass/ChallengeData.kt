package com.umpa2020.tracer.dataClass

data class ChallengeData (
  var id: String? = null,
  var name: String? = null,
  var date: Long? = null,
  var from: Long? = null,
  var to: Long? = null,
  var locale: MutableList<String>? = null,
  var address: String? = null,
  var host: String? = null,
  var intro: String? = null,
  var raceDesc: String? = null,
  var type: String? = null,
  var link: String? = null,

  var arrCategory: MutableList<String>? = null,
  var gpxPath: String? = null,
  var imagePath: String? = null
)

/*
data class ChallengeData (
  var iconsBar: Int? = null,
  var nametxt: String? = null,
  var datetxt: String? = null,
  var dateweek: String? = null
)
*/
