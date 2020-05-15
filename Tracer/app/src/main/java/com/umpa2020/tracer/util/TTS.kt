package com.umpa2020.tracer.util

import android.content.Context.AUDIO_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.preference.PreferenceManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.extensions.show
import java.util.*


object TTS {
  lateinit var tts: TextToSpeech
  private var mAudioManager: AudioManager
  private var mVibrator: Vibrator
  val timings = longArrayOf(100, 100, 0, 400, 0, 200, 0, 400)
  val amplitudes = intArrayOf(0, 50, 0, 100, 0, 50, 0, 150)

  init {
    tts = TextToSpeech(App.instance.context(), TextToSpeech.OnInitListener {
      if (it == TextToSpeech.SUCCESS) {
        //사용할 언어를 설정
        val result = tts.setLanguage(Locale.getDefault())
        //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          "이 언어는 지원하지 않습니다.".show()
        } else {
          //음성 톤
          tts.setPitch(0.7f)
          //읽는 속도
          tts.setSpeechRate(1.2f)
        }
      }
    })
    mAudioManager =
      App.instance.getSystemService(AUDIO_SERVICE) as AudioManager
    mVibrator = App.instance.getSystemService(VIBRATOR_SERVICE) as Vibrator
  }

  @Suppress("DEPRECATION")
  fun speech(sentence: String) {
    // Get the preferences
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.instance.context())

    if (mAudioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) { // 핸드폰이 소리 일 때
      if (prefs.getBoolean("ttsNotificationSetting", true)) {
        tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
        Logg.d(sentence)
      }
    } else if (mAudioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE) { // 핸드폰이 진동 일 때
      if (Build.VERSION.SDK_INT >= O) // 오레오 이상
        mVibrator.vibrate(VibrationEffect.createOneShot(1000,  VibrationEffect.DEFAULT_AMPLITUDE))
      else // 오레도 미만
        mVibrator.vibrate(1000)
    }
  }
}