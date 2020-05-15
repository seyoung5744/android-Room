package com.umpa2020.tracer.main.profile.settting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.UserInfo

class SettingPreferenceFragment : PreferenceFragmentCompat() {
  // firebase Auth
  private var mAuth: FirebaseAuth? = null
  private var mGoogleSignInClient: GoogleSignInClient? = null

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.fragment_setting_preference, rootKey)

    mAuth = FirebaseAuth.getInstance() // FirebaseAuth를 사용하기 위해서 인스턴스를 꼭 받아오기

    //  Configure Google Sign In
    // 구글 로그인 옵션
    // GoogleSignInOptions 옵션을 관리해주는 클래스로 API 키값과 요청할 값이 저장되어 있다
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build()

    mGoogleSignInClient = GoogleSignIn.getClient(App.instance, googleSignInOptions) //구글 로그인 클래스

    // TTS 스위치 버튼
    val switchTTS: SwitchPreference? = findPreference("ttsNotificationSetting") as SwitchPreference?

    // Switch preference change listener
    switchTTS?.setOnPreferenceChangeListener{ preference, newValue ->
      if (newValue == true){
      }else{
      }

      true
    }

    // 잠금 화면 스위치 버튼

    // 알람 스위치 버튼
    val switchNotification: SwitchPreference? = findPreference("notificationSetting") as SwitchPreference?

    // Switch preference change listener
    switchNotification?.setOnPreferenceChangeListener{ preference, newValue ->
      if (newValue == true){
        //TODO 알람 ON 기능 추가
      }else{
        //TODO 알람 OFF 기능 추가
      }

      true
    }
  }

  /**
   * preference 클릭할 때
   */
  override fun onPreferenceTreeClick(preference: androidx.preference.Preference?): Boolean {
    //내 정보 눌렀을 때
    val currentClickTime = SystemClock.uptimeMillis()
    val elapsedTime = currentClickTime - Constants.mLastClickTime
    Constants.mLastClickTime = currentClickTime

    // 중복클릭 아닌 경우
    if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
      if (preference?.key.equals("myInformation")) {
        val intent = Intent(context, MyInformationActivity::class.java)
        startActivity(intent)
      }
    }

    //로그아웃 눌렀을 때
    if (preference?.key.equals("logout")) {
      logOut()
    }

    //회원 탈퇴 눌렀을 때
    if (preference?.key.equals("unregister")) {
      //TODO. 회원 탈퇴 기능 만들기
    }
    return super.onPreferenceTreeClick(preference)
  }

  var noticePopup: ChoicePopup? = null

  private fun logOut() {
    // Shared의 정보 삭제
    noticePopup = ChoicePopup(requireContext(), getString(R.string.please_select), getString(R.string.like_logout), getString(R.string.yes), getString(R.string.no),
      View.OnClickListener {
        // 예
        UserInfo.autoLoginKey = ""
        UserInfo.email = ""
        UserInfo.nickname = ""
        UserInfo.birth = ""
        UserInfo.gender = ""
        //TODO : 이건 정빈이가 추가한거 같은데 삭제 하니깐 약간 어플이 꼬이는거 같아서 물어보고 삭제하든가 하기
//        UserInfo.permission = 0
//        UserInfo.rankingLatLng = LatLng(0.0, 0.0)

        // 이거 해주니깐 다시 로그인할 때 아이디 선택 요구함.
        mGoogleSignInClient!!.signOut()

        noticePopup!!.dismiss()
        // 어플 재 시작
        ActivityCompat.finishAffinity(App.instance.currentActivity() as Activity)
        val intent = context?.packageManager?.getLaunchIntentForPackage(requireContext().packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

      },
      View.OnClickListener {
        // 아니오
        noticePopup!!.dismiss()
      }
    )
    noticePopup!!.show()
  }


}
