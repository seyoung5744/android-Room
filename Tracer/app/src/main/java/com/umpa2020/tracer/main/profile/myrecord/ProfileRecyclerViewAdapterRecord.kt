package com.umpa2020.tracer.main.profile.myrecord

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.main.ranking.RankRecyclerItemClickActivity
import com.umpa2020.tracer.network.FBMapImageRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_profile_user_record_item.view.*
import java.util.*

class ProfileRecyclerViewAdapterRecord(val datas: ArrayList<ActivityData>) :
  RecyclerView.Adapter<ProfileRecyclerViewAdapterRecord.MyViewHolder>() {
  var context: Context? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_profile_user_record_item, parent, false)
    context = parent.context
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val singleItem1 = datas[position]

    val cutted = singleItem1.mapTitle!!.subSequence(0, singleItem1.mapTitle.length- TIMESTAMP_LENGTH) as String
    val time = singleItem1.time!!.toLong().format(Y_M_D)

    //데이터 바인딩
    FBMapImageRepository().getMapImage(holder.mapImageView, singleItem1.mapTitle.toString())

    when (singleItem1.mode) {
      "racing go the distance" -> {
        holder.activityText.text =
          String.format(context!!.getString(R.string.racing_go_the_distance), cutted, time)
      }
      "racing fail" -> {
        holder.activityText.text =
          String.format(context!!.getString(R.string.racing_fail), cutted, time)
      }
      "map save" -> {
        holder.activityText.text =
          String.format(context!!.getString(R.string.map_save), cutted, time)

        //클릭하면 맵 상세보기 페이지로 이동
      }
    }


    //클릭하면 맵 상세보기 페이지로 이동
    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        val nextIntent = Intent(context, RankRecyclerItemClickActivity::class.java)
        nextIntent.putExtra("MapTitle", singleItem1.mapTitle) //mapTitle 정보 인텐트로 넘김
        context!!.startActivity(nextIntent)
      }
    })
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mapImageView = view.profileUserActivityMapImageView!!
    var activityText = view.profileUserActivityTextView!!
  }
}

