package com.umpa2020.tracer.extensions

import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.constant.Constants.Companion.TURNING_ANGLE
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.util.Logg
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

fun Location.toLatLng(): LatLng {
  return LatLng(latitude, longitude)
}

fun WayPoint.toLatLng(): LatLng {
  return LatLng(lat, lon)
}

fun MutableList<LatLng>.bounds(): LatLngBounds {
  var minlat = first().latitude
  var maxlat = last().latitude
  var minlon = first().longitude
  var maxlon = last().longitude
  forEach {
    if (it.latitude < minlat) minlat = it.latitude
    else if (it.latitude > maxlat) maxlat = it.latitude
    if (it.longitude < minlon) minlon = it.longitude
    else if (it.longitude > maxlon) maxlon = it.longitude
  }
  return LatLngBounds(LatLng(minlat, minlon), LatLng(maxlat, maxlon))
}

fun RouteGPX.addCheckPoint(): RouteGPX {
  if (wptList.isNullOrEmpty()) wptList = mutableListOf()
  this.wptList.add(trkList.first().apply {
    name = "Start"
    desc = "Start Point"
    type = START_POINT
  })

  val latlngs = mutableListOf<LatLng>()
  var distance = 0.0
  trkList.forEachIndexed { i, it ->
    if (i > 0) {
      if (i == trkList.size - 1) {
        wptList.add(it.apply {
          name = "Finish"
          desc = "Finish Point"
          type = FINISH_POINT
        })
      } else {
        distance += SphericalUtil.computeDistanceBetween(
          trkList[i - 1].toLatLng(),
          it.toLatLng()
        )
        if (distance > 500) {
          distance = 0.0
          wptList.add(it.apply {
            name = "Distance point"
            desc = "100m"
            type = DISTANCE_POINT
          })
        }
      }
    }
  }
  return this
}

fun RouteGPX.addDirectionSign(): RouteGPX {
  val latlngs = mutableListOf<LatLng>()
  this.trkList.forEach { latlngs.add(it.toLatLng()) }
  val simplifyLatLngs = PolyUtil.simplify(latlngs, 20.0)
  if (wptList.isNullOrEmpty()) wptList = mutableListOf()
  for (i in 1 until simplifyLatLngs.size - 1) {
    val a = simplifyLatLngs[i - 1]
    val b = simplifyLatLngs[i]
    val c = simplifyLatLngs[i + 1]
    //이전과 현재 좌표직선의 기울기
    var preGradient = (atan2((b.latitude - a.latitude), (b.longitude - a.longitude))) * 180 / PI
    //현재와 다음 좌표직선의 기울기
    var postGradient =
      (atan2((c.latitude - b.latitude), (c.longitude - b.longitude))) * 180 / PI
    //만약 값이 범위를 초과하면 보정
    if (postGradient - preGradient < 180.0 || postGradient - preGradient > -180.0) {
      if (preGradient < 0) {
        preGradient += 360.0
      }
      if (postGradient < 0) {
        postGradient += 360.0
      }
    }
    //직선의 회전각
    val angle = postGradient - preGradient
    if (abs(angle) >= TURNING_ANGLE) {
      if (angle > 0) {
        this.wptList.add(
          WayPoint(b.latitude, b.longitude, 0.0, 0.0, "Turning Point", "Turn Left", 0L, TURNING_LEFT_POINT)
        )
      } else {
        this.wptList.add(
          WayPoint(b.latitude, b.longitude, 0.0, 0.0, "Turning Point", "Turn Right", 0L, TURNING_RIGHT_POINT)
        )
      }

    }
  }
  return this
}

fun RouteGPX.classToGpx(folderPath: String): Uri {
  try {
    val document =
      DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .domImplementation.createDocument("test", "gpx", null)
    wptList.forEach { wpt ->
      document.documentElement.appendChild(
        document.createElement("wpt").apply {
          setAttribute("lat", "${wpt.lat}")
          setAttribute("lon", "${wpt.lon}")
          appendChild(document.createElement("ele").apply { appendChild(document.createTextNode("${wpt.alt}")) })
          appendChild(document.createElement("time").apply { appendChild(document.createTextNode("${wpt.time}")) })
          appendChild(document.createElement("name").apply { appendChild(document.createTextNode(wpt.name)) })
          appendChild(document.createElement("desc").apply { appendChild(document.createTextNode(wpt.desc)) })
          appendChild(document.createElement("type").apply { appendChild(document.createTextNode("${wpt.type?.ordinal}")) })
        })
    }

    val trkseg = document.createElement("trkseg")
    trkList.forEach { wpt ->
      trkseg.appendChild(document.createElement("trkpt").apply {
        setAttribute("lat", "${wpt.lat}")
        setAttribute("lon", "${wpt.lon}")
        appendChild(document.createElement("ele").apply { appendChild(document.createTextNode("${wpt.alt}")) })
        appendChild(document.createElement("speed").apply { appendChild(document.createTextNode("${wpt.speed}")) })
        appendChild(document.createElement("type").apply { appendChild(document.createTextNode("${wpt.type?.ordinal}")) })
      })
    }
    document.documentElement.appendChild(document.createElement("trk").appendChild(trkseg))

    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route_${System.currentTimeMillis()}.gpx"
    val file = File(saveFolder, path)         //로컬에 파일저장

    val transformer = TransformerFactory.newInstance().newTransformer()
    val source = DOMSource(document)
    val result = StreamResult(FileOutputStream(file))
    // val result = StreamResult(System.out)
    transformer.transform(source, result)
    Logg.d("tlqkf")
    return Uri.fromFile(file)
  } catch (e: Exception) {
    e.printStackTrace()
  }

/*
  val gpxBuilder = GPX.builder()
    .addTrack { it.addSegment(TrackSegment.of(trkList)).build() }
  wptList.forEach { gpxBuilder.addWayPoint(it) }
  val gpx = gpxBuilder.build()
  try {
    Logg.d("make gpx file")
    val saveFolder = File(folderPath) // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    val path = "route_${System.currentTimeMillis()}.gpx"
    val myfile = File(saveFolder, path)         //로컬에 파일저장
    GPX.write(gpx, (myfile.path))
    Logg.d("start upload gpx")
    return Uri.fromFile(myfile)
  } catch (e: Exception) {
    Logg.d(e.toString());
  }*/
  return Uri.EMPTY
}

fun String.gpxToClass(): RouteGPX {
  val inputStream = FileInputStream(this)
  val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
  val wptList = mutableListOf<WayPoint>()
  dom.documentElement.getElementsByTagName("wpt").let {
    for (i in 0 until it.length) {
      wptList.add(
        it.item(i).let { node ->
          node.toWayPoint()
        })
      /*WayPoint(
        node.value("lat").toDouble(),
        node.value("lon").toDouble(),
        node.value("ele").toDouble(),
        node.value("speed").toDouble(),
        node.value("name"),
        node.value("desc"),
        node.value("time").toLong(),
        WayPointType.values()[node.value("type").toInt()]
      )*/
    }
  }
  val trkList = mutableListOf<WayPoint>()
  dom.documentElement.getElementsByTagName("trkpt").let {
    for (i in 0 until it.length) {
      trkList.add(it.item(i).toWayPoint())
    }
  }
  return RouteGPX(0, "", wptList, trkList)
}

fun Node.toWayPoint(): WayPoint {
  val lat = value("lat")!!.toDouble()
  val lng = value("lon")!!.toDouble()
  var ele: Double? = null
  var speed: Double? = null
  var time: Long? = null
  var name: String? = null
  var desc: String? = null
  var type: WayPointType? = null
  for (i in 0 until childNodes.length) {
    val item = childNodes.item(i)
    when (item.nodeName) {
      "ele" -> {
        ele = item.textContent.toDouble()
      }
      "speed" -> {
        speed = item.textContent.toDouble()
      }
      "time" -> {
        time = item.textContent.toLong()
      }
      "name" -> {
        name = item.textContent
      }
      "desc" -> {
        desc = item.textContent
      }
      "type" -> {
        type = values()[item.textContent.toInt()]
      }
      else -> {
      }
    }
  }
  return WayPoint(lat, lng, ele!!, speed, name, desc, time, type)
}

fun Node.value(name: String): String? {
  return attributes.getNamedItem(name)?.nodeValue

}

fun Location.toWayPoint(type: WayPointType): WayPoint {
  return when (type) {
    START_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Start", "Start Point", time, type)
    FINISH_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Finish", "Finish Point", time, type)
    DISTANCE_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Distance", "Distance Point", time, type)
    TURNING_LEFT_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Turning Point", "Turn Left", time, type)
    TURNING_RIGHT_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Turning Point", "Turn Right", time, type)
    TRACK_POINT -> WayPoint(latitude, longitude, altitude, speed.toDouble(), "Track Point", "Track Point", time, type)
  }
}