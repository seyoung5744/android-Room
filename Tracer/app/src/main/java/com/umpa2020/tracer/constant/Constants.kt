package com.umpa2020.tracer.constant

class Constants {
  companion object {
    const val NICKNAME_RULE = "^(?=.*\\W).{3,12}\$"

    // 성별 제한
    const val GENDER_RULE = "^(?=.*[0-9]).{0,10}\$"

    // 나이 제한
    const val AGE_RULE = "^(?=.*\\d).{1,2}\$"

    //체크포인트에 도착 범위(단위 : 미터)
    const val ARRIVE_BOUNDARY = 10

    //경로이탈 범위(단위 : 미터)
    const val DEVIATION_DISTANCE = 20.0

    //경로이탈 카운트
    const val DEVIATION_COUNT = 30
    const val WPINTERVAL = 100  //way point interval

    //handler what
    const val INFOUPDATE = 1 //distance and speed update
    const val RACINGFINISH = 2 //Racing is finished
    const val DEVIATION = 3 //racer deviate the track
    const val MAPISREADY = 4 //on map ready call back

    //최대 거리
    const val MAX_DISTANCE = 100000

    //seekerBar Max
    const val MAX_SEEKERBAR = 100

    //animation duration
    const val ANIMATION_DURATION_TIME = 500L

    // ProfileFragment id
    const val PROFILE_FRAGMENT_ID = 2131296382

    // BaseRunningActivity 일시정지 애니메이션 상수
    const val PAUSE_ANIMATION_DURATION_TIME = 800L

    //위치 받아오는 주기
    const val LOCATION_INTERAL = 1000

    //REQUEST 성공
    const val SUCCESS_REQUEST = 1

    // 최소 정지 거리 m
    const val MINIMUM_STOPPING_DISTANCE = 200

    //회전 판정 각도
    const val TURNING_ANGLE = 30

    //중복 클릭 방지 시간 설정 ( 해당 시간 이후에 다시 클릭 가능 )
    const val MIN_CLICK_INTERVAL: Long = 600
    var mLastClickTime: Long = 0



    const val TIMESTAMP_LENGTH = 13
    var testId = 0
  }
}
