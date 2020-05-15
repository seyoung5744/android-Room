package com.umpa2020.tracer.main.profile.myrecord

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.main.profile.myroute.ProfileRecyclerViewAdapterRoute
import com.umpa2020.tracer.network.ActivityListener
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUserActivityRepository
import com.umpa2020.tracer.network.ProfileRouteListener
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_profile_record.*
import kotlinx.android.synthetic.main.activity_profile_route.*

class ProfileRecordActivity : AppCompatActivity() {
  val activity = this
  val rootActivityDatas = arrayListOf<ActivityData>()
  val progressbar = MyProgressBar()
  var isLoding = false
  val userActivityRepo = FBUserActivityRepository()
  var limit = 0L


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile_record)

    progressbar.show()
    profileRecyclerRecord.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!profileRecyclerRecord.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!profileRecyclerRecord.canScrollVertically(1)) {
          // 리사이클러뷰가 맨 아래로 이동했을 경우
          if (!isLoding) {
            userActivityRepo.listUserMakingActivity(activityListener, 15)
          }
          isLoding = true
        }
      }
    })
  }

  override fun onResume() {
    limit = rootActivityDatas.size.toLong()

    if (limit == 0L) limit = 15L
    else rootActivityDatas.clear()

    userActivityRepo.listUserMakingActivityFirst(activityListener, limit)
    super.onResume()
  }

  private val activityListener = object : ActivityListener {
    override fun activityList(activityDatas: ArrayList<ActivityData>) {
      rootActivityDatas.addAll(activityDatas)

      Logg.d("ssmm11 size = ${activityDatas.size}")
      if (activityDatas.isEmpty()) {
        profileRecyclerActivityisEmpty.visibility = View.VISIBLE
        progressbar.dismiss()
      } else {
        if (rootActivityDatas.size < 16) {
          //adpater 추가
          profileRecyclerRecord.adapter = ProfileRecyclerViewAdapterRecord(rootActivityDatas)
          profileRecyclerRecord.layoutManager = LinearLayoutManager(activity)
          profileRecyclerActivityisEmpty.visibility = View.GONE
        } else {
          profileRecyclerRecord.adapter!!.notifyDataSetChanged()
        }
        isLoding = false
        progressbar.dismiss()
      }

    }
  }


}
