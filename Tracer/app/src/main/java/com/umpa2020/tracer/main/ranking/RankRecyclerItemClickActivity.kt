package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*

class RankRecyclerItemClickActivity : AppCompatActivity(), OnSingleClickListener {
  lateinit var progressbar: ProgressBar
  val activity = this
  var likes = 0
  var mapTitle = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)
    progressbar = ProgressBar(this)
    rankRecyclerMoreButton.setOnClickListener(this)
    rankRecyclerHeart.setOnClickListener(this)

    progressbar.show()
    val intent = intent
    //전달 받은 값으로 Title 설정
    mapTitle = intent.extras?.getString("MapTitle").toString()

    val cutted = mapTitle.subSequence(0, mapTitle.length - TIMESTAMP_LENGTH)
    rankRecyclerMapTitle.text = cutted

    // 맵 이미지 DB에서 받아와서 설정
    val imageView = rankRoutePriview
    FBMapImageRepository().getMapImage(imageView, mapTitle)

    // 해당 맵 좋아요 눌렀는지 확인
    FBLikesRepository().getMapLike(mapTitle, likedMapListener)

    FBMapRankingRepository().listMapRanking(mapTitle, mapRankingListener)

    val db = FirebaseFirestore.getInstance()

    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          // 해당 맵의 메이커 닉네임, 프로필 이미지 주소를 받아온다.
          rankRecyclerNickname.text = document.get("makersNickname") as String
          FBProfileRepository().getProfileImage(
            rankRecyclerProfileImage,
            rankRecyclerNickname.text.toString()
          )
        }
      }
  }

  override fun onResume() {
    super.onResume()
    Logg.d("onResume()")
  }

  override fun onPause() {
    super.onPause()
    Logg.d("onPause()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.rankRecyclerMoreButton -> {
        val nextIntent = Intent(App.instance.context(), RankingMapDetailActivity::class.java)
        nextIntent.putExtra("MapTitle", mapTitle)
        startActivity(nextIntent)
      }
      R.id.rankRecyclerHeart -> {
        if (rankRecyclerHeartSwitch.text == "off") {
          FBLikesRepository().updateLikes(mapTitle, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
          rankRecyclerHeartSwitch.text = "on"
          likes++
          rankRecyclerHeartCount.text = likes.toString()
        } else if (rankRecyclerHeartSwitch.text == "on") {
          FBLikesRepository().updateNotLikes(mapTitle, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
          rankRecyclerHeartSwitch.text = "off"
          likes--
          rankRecyclerHeartCount.text = likes.toString()
        }
      }
    }
  }

  /**
   * 먼저 현재 사용자가 좋아요를 눌렀는지 서버에서 받아온 뒤,
   * 핸들러로 값을 받아 화면에 표시한 후,
   * (이 작업이 조금 오래걸려서 프로그래스바를 여기서 dismiss)
   */

  private val likedMapListener = object : LikedMapListener {
    override fun likedList(likedMaps: List<LikedMapData>) {
      TODO("Not yet implemented")
    }

    override fun liked(liked: Boolean, getlikes: Int) {
      rankRecyclerHeartCount.text = getlikes.toString()
      //adpater 추가
      likes = getlikes
      if (liked) {
        rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
        rankRecyclerHeartSwitch.text = "on"
      } else {
        rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        rankRecyclerHeartSwitch.text = "off"
      }
      progressbar.dismiss()
    }
  }

  private val mapRankingListener = object : MapRankingListener {
    override fun getMapRank(arrRankingData: ArrayList<RankingData>) {
      //레이아웃 매니저 추가
      rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(activity)
      //adpater 추가
      rankRecyclerItemClickRecyclerView.adapter =
        RankRecyclerViewAdapterTopPlayer(arrRankingData, mapTitle)
    }
  }
}
