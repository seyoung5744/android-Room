package com.umpa2020.tracer.main.start.running


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.customUI.WorkaroundMapFragment
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.network.FBUserActivityRepository
import com.umpa2020.tracer.util.*
import kotlinx.android.synthetic.main.activity_running_save.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import com.umpa2020.tracer.gpx.WayPointType.*

class RunningSaveActivity : AppCompatActivity(), OnMapReadyCallback, OnSingleClickListener {
  var switch = 0

  lateinit var infoData: InfoData
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap
  val speedList = mutableListOf<Double>()
  lateinit var progressBar: MyProgressBar


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(com.umpa2020.tracer.R.layout.activity_running_save)
    progressBar = MyProgressBar()
    infoData = intent.getParcelableExtra("InfoData")!!
    routeGPX = intent.getParcelableExtra("RouteGPX")!!
    val wmf =
      supportFragmentManager.findFragmentById(com.umpa2020.tracer.R.id.map_viewer) as WorkaroundMapFragment
    wmf.getMapAsync(this)
    wmf.setListener(object : WorkaroundMapFragment.OnTouchListener {
      override fun onTouch() {
        runningSaveScrollView.requestDisallowInterceptTouchEvent(true);
      }
    })

    val elevationList = mutableListOf<Double>()
    routeGPX.trkList.forEach {
      speedList.add(it.speed!!)
      elevationList.add(it.alt)
    }
    distance_tv.text = infoData.distance!!.prettyDistance

    time_tv.text = infoData.time!!.format(m_s)
    speed_tv.text = String.format("%.2f", speedList.average())
    if (infoData.privacy == Privacy.PUBLIC) {
      racingRadio.isChecked = false
      racingRadio.isEnabled = false
      publicRadio.isChecked = true
    }
    val myChart = Chart(elevationList, speedList, chart)
    myChart.setChart()
    save_btn.setOnClickListener(this)
    runningSaveDeleteButton.setOnClickListener(this)
  }

  override fun onBackPressed() {
    if (::noticePopup.isInitialized && noticePopup.isShowing) {
      noticePopup.dismiss()
    } else {
      deleteSaving()
    }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.runningSaveDeleteButton -> {
        deleteSaving()
      }
      R.id.save_btn -> {
        if (mapTitleEdit.text.toString() == "") {
          mapTitleEdit.hint = "제목을 설정해주세요"
          mapTitleEdit.setHintTextColor(Color.RED)
        } else if (mapExplanationEdit.text.toString() == "") {
          mapExplanationEdit.hint = "맵 설명을 작성해주세요"
          mapExplanationEdit.setHintTextColor(Color.RED)
        } else {
          progressBar.show()
          val callback = GoogleMap.SnapshotReadyCallback {
            try {
              //save_btn.isEnabled = false
              val saveFolder = File(filesDir, "mapdata") // 저장 경로
              if (!saveFolder.exists()) {       //폴더 없으면 생성
                saveFolder.mkdir()
              }
              val path = "racingMap" + saveFolder.list()!!.size + ".bmp"        //파일명 생성하는건데 수정필요
              //비트맵 크기에 맞게 잘라야함
              val myfile = File(saveFolder, path)                //로컬에 파일저장
              val out = FileOutputStream(myfile)
              it.compress(Bitmap.CompressFormat.PNG, 90, out)
              save(myfile.path)
            } catch (e: Exception) {
              Logg.d(e.toString())
            }
          }
          traceMap.captureMapScreen(callback)
        }
      }
    }
  }

  lateinit var noticePopup: ChoicePopup
  private fun deleteSaving() {
    noticePopup = ChoicePopup(this, getString(R.string.select_type),
      getString(R.string.Are_you_sure_to_delete_this),
      getString(R.string.yes), getString(R.string.no),
      View.OnClickListener {
        //랭킹 기록용 버튼 눌렀을 때
        val intent = Intent(App.instance.context(), MainActivity::class.java)
        noticePopup.dismiss()
        startActivity(intent)
        finish()
      },
      View.OnClickListener {
        noticePopup.dismiss()
      })
    noticePopup.show()
  }


  fun save(imgPath: String) {
    Logg.d("Start Saving")
    // 타임스탬프 찍는 코드
    val timestamp = Date().time

    // 인포데이터에 필요한 내용을 저장하고
    infoData.makersNickname = UserInfo.nickname
    infoData.mapTitle = mapTitleEdit.text.toString() + timestamp.toString()
    infoData.mapImage = "mapImage/${infoData.mapTitle}"
    infoData.mapExplanation = mapExplanationEdit.text.toString()
    infoData.makersNickname = UserInfo.nickname
    infoData.execute = 0
    infoData.likes = 0

    when (privacyRadioGroup.checkedRadioButtonId) {
      R.id.racingRadio -> infoData.privacy = Privacy.RACING
      R.id.publicRadio -> infoData.privacy = Privacy.PUBLIC
      R.id.privateRadio -> infoData.privacy = Privacy.PRIVATE
    }

    // 맵 타이틀과, 랭킹 중복 방지를 위해서 시간 값을 넣어서 중복 방지
    val saveFolder = File(baseContext.filesDir, "routeGPX") // 저장 경로
    if (!saveFolder.exists()) {       //폴더 없으면 생성
      saveFolder.mkdir()
    }
    routeGPX.addDirectionSign()
    routeGPX.wptList.forEach {
      Logg.d("lat : ${it.lat} lon : ${it.lon} desc : ${it.desc} ")
    }
    val routeGpxFile = routeGPX.classToGpx(saveFolder.path)

    // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
    val fstorage = FirebaseStorage.getInstance()
    val fRef = fstorage.reference.child("mapRoute")
      .child(infoData.mapTitle!!).child(infoData.mapTitle!!)
    infoData.routeGPXPath = fRef.path

    val fuploadTask = fRef.putFile(routeGpxFile)

    fuploadTask.addOnFailureListener {
      Logg.d("Fail : $it")
    }.addOnSuccessListener {
      Logg.d("Success")
    }
    // db에 그려진 맵 저장하는 스레드 - 여기서는 실제 그려진 것 보다 후 보정을 통해서
    // 간략화 된 맵을 업로드 합니다.
    val db = FirebaseFirestore.getInstance()

    // InfoData class upload to database 참조 - 루트를 제외한 맵 정보 기술
    //TODO: routeGPX 파일로 스토리지에 업로드 후 경로 infoData에 넣어서 set
    db.collection("mapInfo").document(infoData.mapTitle!!).set(infoData)

    // 히스토리 업로드
    val activityData = ActivityData(infoData.mapTitle, timestamp.toString(), "map save")
    FBUserActivityRepository().createUserHistory(activityData)

    val fRefRanking = fstorage.reference.child("mapRoute")
      .child(infoData.mapTitle!!).child("racingGPX").child(UserInfo.autoLoginKey)
    val fRankinguploadTask = fRefRanking.putFile(routeGpxFile)
    fRankinguploadTask.addOnFailureListener {
      Logg.d("Fail : $it")
    }.addOnSuccessListener {
      Logg.d("Success")
    }
    // db에 그려진 맵 저장하는 스레드 - 여기서는 실제 그려진 것 보다 후 보정을 통해서
    // 간략화 된 맵을 업로드 합니다.
    val rankingData = RankingData(
      UserInfo.nickname,
      UserInfo.autoLoginKey,
      UserInfo.nickname,
      infoData.time,
      true,
      speedList.max().toString(),
      speedList.average().toString(),
      fRef.path
    )

    //TODO : firebase로
    db.collection("rankingMap").document(infoData.mapTitle!!).set(rankingData)
    db.collection("rankingMap").document(infoData.mapTitle!!).collection("ranking")
      .document(UserInfo.autoLoginKey + timestamp).set(rankingData)

    // db에 원하는 경로 및, 문서로 업로드

    // storage에 이미지 업로드 모든 맵 이미지는 mapimage/maptitle로 업로드가 된다.
    val storage = FirebaseStorage.getInstance()
    val mapImageRef = storage.reference.child(infoData.mapImage.toString())
    val uploadTask = mapImageRef.putFile(Uri.fromFile(File(imgPath)))
    uploadTask.addOnFailureListener {
      Logg.d("스토리지 실패 = " + it.toString())
    }.addOnSuccessListener {
      progressBar.dismiss()
      finish()
    }

    // 위에 지정한 스레드 스타트
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap.drawRoute(routeGPX.trkList.toList(), routeGPX.wptList.filter { it.type== START_POINT||it.type==FINISH_POINT})
  }
}