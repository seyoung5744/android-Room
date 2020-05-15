package com.umpa2020.tracer.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.choice_popup.*

/**
 *  text에는 제목도
 */
class ChoicePopup(context: Context,
                  private val titleText:String,
                  private val bodyText:String,
                  private val yesText:String,
                  private val noText:String,
                  private val yes:View.OnClickListener,
                  private val no:View.OnClickListener) : Dialog(context){
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.choice_popup)
    runningActivityPopUpTitle.text = titleText
    runningActivityPopUpTextView.text=bodyText
    runningActivityYesButton.text = yesText
    runningActivityNoButton.text = noText
    runningActivityYesButton.setOnClickListener(yes)
    runningActivityNoButton.setOnClickListener(no)
  }
}