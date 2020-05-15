package com.umpa2020.tracer.main.challenge

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.network.FBChallengeImageRepository
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_challengefragment_item.view.*

class ChallengeRecyclerViewAdapter(var challenge: MutableList<ChallengeData>) :
  RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ItemHolder>() {


  var context: Context? = null


  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val singleItem1 = challenge[position]

    FBChallengeImageRepository().getChallengeImage(holder.icons, singleItem1.imagePath!!)
    holder.name.text = singleItem1.name
    holder.date.text = singleItem1.date!!.format(M_D)
    holder.locale.text = "${singleItem1.locale!![0]} ${singleItem1.locale!![1]}"

    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        val nextIntent = Intent(context, ChallengeRecycleritemClickActivity::class.java)
        nextIntent.putExtra("challengeId", singleItem1.id)
        context!!.startActivity(nextIntent)
      }
    })

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val itemHolder = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_challengefragment_item, parent, false)
    context = parent.context
    return ItemHolder(itemHolder)
  }

  override fun getItemCount(): Int {
    return challenge.size
  }


  inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var icons = view.challenge_map
    var name = view.challenge_race_name
    var date = view.challenge_date
    var locale = view.challenge_locale
  }

}
