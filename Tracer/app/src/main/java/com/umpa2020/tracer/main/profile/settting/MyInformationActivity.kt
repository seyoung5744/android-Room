package com.umpa2020.tracer.main.profile.settting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.toAge
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.ProfileListener
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_my_information.*
import kotlinx.android.synthetic.main.signup_toolbar.*
import kotlinx.android.synthetic.main.signup_toolbar.view.*

class MyInformationActivity : AppCompatActivity(), OnSingleClickListener {
  lateinit var progressBar: ProgressBar
  private var selectedImageUri: Uri? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_information)
    app_toolbar.titleText.text = getString(R.string.my_information)
    // 프로필 이미지 서버에서 가져와서 화면에 설정
    FBProfileRepository().getProfileImage(profileImage, UserInfo.nickname)

    // Shared에 저장된 유저 정보 설정정
    emailTextView.text = UserInfo.email
    nickNameTextView.text = UserInfo.nickname
    ageTextView.text = toAge(UserInfo.birth)
    genderTextView.text = UserInfo.gender

    // 버튼 리스너 초기화
    backImageBtn.setOnClickListener(this)
    profileImage.setOnClickListener(this)
    profileChangeButton.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.backImageBtn -> {
        finish()
      }
      R.id.profileImage -> {
        Logg.i("앨범으로")
        goToAlbum()
      }
      R.id.profileChangeButton -> {
        if (selectedImageUri != null) { // 사진을 고르면
          progressBar = ProgressBar(App.instance.currentActivity() as Activity)
          progressBar.show()
          FBProfileRepository().updateProfileImage(selectedImageUri, profileListener)
        } else { // 사진을 안고르면
          finish()
        }
      }
    }
  }

  /**
   * 카메라 접근 권한 and 앨범 접근
   */
  private fun goToAlbum() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    startActivityForResult(intent, PICK_FROM_ALBUM)
  }

  // intent 결과 받기
  override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
    super.onActivityResult(requestCode, resultCode, intentData)
    // 앨범
    if (requestCode == PICK_FROM_ALBUM) {
      if (resultCode == RESULT_OK) {
        if (intentData != null) {
          selectedImageUri = intentData.data
          profileImage.setImageURI(selectedImageUri)
          profileImage.scaleType = ImageView.ScaleType.CENTER_CROP

        }
      } else if (resultCode == RESULT_CANCELED) {
        //사진 선택 취소
        Toast.makeText(this, getString(R.string.deselect_photo), Toast.LENGTH_LONG).show()
      }
    }
  }

  companion object {
    // 카메라 requestCode
    private const val PICK_FROM_ALBUM = 1
  }

  private val profileListener = object : ProfileListener {
    override fun getProfile(distance: Double, time: Double) {
    }

    override fun changeProfile() {
      progressBar.dismiss()
      finish()
    }
  }
}
