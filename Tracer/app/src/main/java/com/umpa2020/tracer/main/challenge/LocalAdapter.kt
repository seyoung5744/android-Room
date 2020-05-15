package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_localname_item.view.*


class LocalAdapter(var localeList: Array<String>, val onClickListener: View.OnClickListener) :
  RecyclerView.Adapter<LocalAdapter.ItemHolder>() {
  var context: Context? = null

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    holder.name.text = localeList[position]
    holder.name.setOnClickListener(onClickListener)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val itemHolder = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_localname_item, parent, false)
    return ItemHolder(itemHolder)
  }

  override fun getItemCount(): Int {
    return localeList.size
  }

  inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var name = view.local_name
  }
}