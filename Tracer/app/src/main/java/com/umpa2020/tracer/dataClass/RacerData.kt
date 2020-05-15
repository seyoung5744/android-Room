package com.umpa2020.tracer.dataClass

import java.io.Serializable


data class RacerData(
  var racerId: String? = null,
  var racerName: String? = null
) : Serializable