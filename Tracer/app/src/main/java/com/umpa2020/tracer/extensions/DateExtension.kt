package com.umpa2020.tracer.extensions

import android.annotation.SuppressLint
import com.umpa2020.tracer.util.Logg
import java.text.SimpleDateFormat
import java.util.*

const val YEAR_MONTH_DAY_KR = "yyyy년 MM월 dd일"
const val MONTH_DAY_KR = "MM월 dd일"
const val YEAR_MONTH_DAY = "MMM dd, yyyy"
const val YEAR = "yyyy"
const val MONTH_DAY = "MMM dd"

const val mm_ss = "mm:ss"

const val Y_M_D = 0
const val M_D = 1
const val m_s = 2

/**
 * 날짜 시간 포맷 적용
 *
 * @param pattern yyyy-MM-dd'T'HH:mm:ssZ
 * @return 2020-03-21T18:34:10+0900
 */


fun Long.format(pattern: Int): String {
  val locale = Locale.getDefault()
  val type = when (pattern) {
    Y_M_D -> {
      when (locale) {
        Locale.KOREA -> YEAR_MONTH_DAY_KR
        else -> YEAR_MONTH_DAY
      }
    }
    //올해면 월, 일 만
    M_D -> {
      val date = Date()
      //올해인지 검사
      if (SimpleDateFormat(YEAR, Locale.getDefault()).format(this)
        == SimpleDateFormat(YEAR, Locale.getDefault()).format(date.time)
      ) {
        when (locale) {
          Locale.KOREA -> MONTH_DAY_KR
          else -> MONTH_DAY
        }
      } else {
        when (locale) {
          Locale.KOREA -> YEAR_MONTH_DAY_KR
          else -> YEAR_MONTH_DAY
        }
      }
    }
    m_s -> mm_ss
    else -> YEAR_MONTH_DAY

  }
  return this.format(type)
}

fun Long.format(pattern: String): String {
  return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

/**
 * 날짜 문자열을 millisecond 로 변환
 *
 * @param pattern yyyy-MM-dd HH:mm:ss
 * @return 1584783920000
 */
fun String.toMillisecond(pattern: String): Long? {
  try {
    return SimpleDateFormat(pattern, Locale.getDefault()).parse(this).time
  } catch (ignore: Exception) {
  }
  return null
}

/**
 * 날짜 문자열을 다른 포맷으로 변경
 *
 * @param from yyyy-MM-dd HH:mm:ss
 * @param to   yyyy/MM/dd
 * @return 2018/01/10
 */
fun String.format(from: String, to: String): String? {
  return toMillisecond(from)?.format(to)
}
/**
 * Int형 yyyy m d 를 String형 yyyyMMdd로 변환.
 * 십의 자리 미만의 월, 일 앞에 0을 붙이는 함수.
 */

fun intToyyyyMMdd(year : Int, month : Int, day : Int) : String?{
  val mm = if (month < 10)
    "0$month"
  else
    month.toString()

  val dd = if(day < 10)
    "0$day"
  else
    day.toString()

  return "$year$mm$dd"
}
/**
 *  년월일을 나이로 변경
 *
 *  @param from yyyyMMdd
 *  @param to Age
 *  @return 00
 */

@SuppressLint("SimpleDateFormat")
fun toAge(birth: String): String? {
  var today = "" // 오늘 날짜
  var manAge = 0 // 만 나이

  val formatter = SimpleDateFormat("yyyyMMdd")
  today = formatter.format(Date()) // 시스템 날짜를 가져와서 yyyyMMdd
  val todayYear = Integer.parseInt(today.substring(0, 4))
  val todayMonth = Integer.parseInt(today.substring(4, 6))
  val todayDay = Integer.parseInt(today.substring(6, 8))

  val year = Integer.parseInt(birth.substring(0, 4))
  val month = Integer.parseInt(birth.substring(4, 6))
  val day = Integer.parseInt(birth.substring(6, 8))

  manAge = todayYear - year

  Logg.d("${todayMonth} ")

  if (todayMonth < month) { // 생년월일 "월"이 지났는지 체크
    manAge--
  } else if (todayMonth == month) { // 생년월일 "일"이 지났는지 체크
    if (todayDay < day) {
      manAge-- // 생일 안지났으면 (만나이 - 1)
    }
  }

//  return (manAge + 1).toString() // 한국나이를 측정하기 위해서 +1살 (+1을 하지 않으면 외국나이 적용됨)
  return manAge.toString()
}