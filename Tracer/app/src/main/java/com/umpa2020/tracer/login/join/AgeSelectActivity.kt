package com.umpa2020.tracer.login.join

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.intToyyyyMMdd
import com.umpa2020.tracer.extensions.toAge
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_age_select.*
import kotlinx.android.synthetic.main.signup_toolbar.*
import kotlinx.android.synthetic.main.signup_toolbar.view.*

class AgeSelectActivity : AppCompatActivity(), OnSingleClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_age_select)

    backImageBtn.setOnClickListener(this)
    nextButton.setOnClickListener(this)

    app_toolbar.titleText.text = getString(R.string.txtInputInfoAge)

    yearPicker.apply {
      minValue = 1950
      maxValue = 2020
      //끝 숫자부터 다음에 시작 숫자로 순환하지 않게 하는 옵션은 wrapSelectorWheel 이다.
      // 이 값을 false로 설정하면 NumberPicker의 범위가 시작~끝으로 고정된다.
      wrapSelectorWheel = true
      // 또, NumberPicker의 기본 설정은 EditText로 되어 있어서 스크롤 뿐만 아니라 키보드를 통해서도 변경이 가능하다.
      // 이 기능은 descendantFocusability 값을 변경하는 것으로 해제할 수 있다
      descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    monthPicker.apply {
      minValue = 1
      maxValue = 12
      setFormatter { i -> String.format("%02d", i) }
      wrapSelectorWheel = true
      descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
    dayPicker.apply {
      minValue = 1
      maxValue = 31
      setFormatter { i -> String.format("%02d", i) }
      wrapSelectorWheel = true
      descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.backImageBtn -> {
        finish()
      }
      R.id.nextButton -> {
        try {
          val intent = Intent()

          // numberPicker에서 Int형으로 값을 가져오므로 1 -> 01과 같은 형식으로 변환.
          // 나중에 이런 yyyyMMdd형식으로 쓰일꺼 같아서 변환해둠.
          val birth = intToyyyyMMdd(yearPicker.value, monthPicker.value, dayPicker.value)
          Logg.d(intToyyyyMMdd(yearPicker.value, monthPicker.value, dayPicker.value)) // 19900102 or 19900112

          Logg.d("만" + toAge(birth!!)) // 19900102 => 나이로

          // yyyyMMdd 형식 전달.
          intent.putExtra("Age", birth)
          setResult(RESULT_OK, intent)
          finish()
        } catch (e: Exception) {
          e.stackTrace
        }


      }
    }
  }
}
