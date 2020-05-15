package com.umpa2020.tracer.login.join

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jakewharton.rxbinding2.widget.RxTextView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.extensions.toAge
import com.umpa2020.tracer.extensions.show
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUserInfoRepository
import com.umpa2020.tracer.util.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.signup_toolbar.*
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), OnSingleClickListener {

  private var WSY = "WSY"
  var success_request = 0

  // 여러 디스포저블 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 합니다.
  internal val viewDisposables = CompositeDisposable()

  private lateinit var inputDataField: Array<EditText>
  private lateinit var textInputLayoutArray: Array<TextInputLayout>
  private lateinit var inputInfoMessage: Array<String>
  private var isInputCorrectData: Array<Boolean> = arrayOf(false, false, false)

  // 카메라 requestCode
  private val PICK_FROM_ALBUM = 1

  private var selectedImageUri: Uri? = null

  private var nickname: String? = null
  private var age: String? = null
  private var gender: String? = null

  // firebase DB
  private var mFirestoreDB: FirebaseFirestore? = null

  //  firebase Storage
  private var mStorage: FirebaseStorage? = null
  private var mStorageReference: StorageReference? = null


  // 초기 가입자인 경우 LoginActivity에서 uid, email을 넘겨 받음
  private var uid: String? = null
  private var email: String? = null

  private lateinit var progressbar: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    setContentView(R.layout.activity_sign_up)

    progressbar = ProgressBar(this)

    editNickname.requestFocus()
    init()

    editNickname.addTextChangedListener(object : TextWatcher {
      override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        flag = 3
        Logg.d(flag.toString())
      }

      override fun afterTextChanged(p0: Editable?) {

      }

      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

      }
    })

    backImageBtn.setOnClickListener(this)
    editAge.setOnClickListener(this)
    editGender.setOnClickListener(this)
    profileImage.setOnClickListener(this)
    redundantCheckButton.setOnClickListener(this)
    sign_up_button.setOnClickListener(this)
  }

  private fun init() {
    /**
     *  Firestore 초기화
     */

    mFirestoreDB = FirebaseFirestore.getInstance()

    /**
     *  Firebase Storage 초기화
     */

    mStorage = FirebaseStorage.getInstance()
    mStorageReference = mStorage!!.reference

    /**
     *  회원가입 Input관련 초기화
     */
    inputDataField = arrayOf(editNickname, editAge, editGender)
    textInputLayoutArray =
      arrayOf(nicknameTextInput, ageTextInput, genderTextInput)
    inputInfoMessage = arrayOf(
      getString(R.string.txtInputInfoNick),
      getString(R.string.txtInputInfoAge),
      getString(R.string.txtInputInfoGender)
    )

    typingListener()

    /**
     *   초기 가입자인 경우 LoginActivity에서 uid, email을 넘겨 받음
     */
    val intent = intent
    uid = intent.getStringExtra("user UID")
    email = intent.getStringExtra("email")
  }

  /**
   * 카메라 접근 권한 and 앨범 접근
   */
  private fun goToAlbum() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    //intent.type = MediaStore.Images.Media.CONTENT_TYPE
    startActivityForResult(intent, PICK_FROM_ALBUM)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    success_request = requestCode
    if (requestCode == success_request) {
      val length = permissions.size
      for (i in 0 until length - success_request) {
        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
          // 동의
          Logg.d("권한 허용 : " + permissions[i])
          goToAlbum()
        }
      }
    }
  }

  /**
   *  앨범 접근 권한
   */
  private fun checkSelfPermission() {
    var temp = ""

    // 파일 읽기 권한 확인
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
    }

    Logg.d(temp)
    if (!TextUtils.isEmpty(temp)) {
      // 권한 요청
      ActivityCompat.requestPermissions(this, temp.trim().split(" ").toTypedArray(), 1)
    } else {
      // 모든 허용 상태
      goToAlbum()
    }
  }


  /**
   * 각 필드별 회원가입 조건이 맞는지 비동기 체크
   */
  private fun typingListener() {
    //Nickname
    val disposableNick = RxTextView.textChanges(inputDataField[0])
      .map { t -> t.isEmpty() || !Pattern.matches(Constants.NICKNAME_RULE, t) }
      .subscribe({
        //inputDataField[2].setText("")
        reactiveInputTextViewData(0, it)
      }) {
        //Error Block
        it.printStackTrace()
      }

    val disposableAge = RxTextView.textChanges(inputDataField[1])
      .map { t -> t.isEmpty() || Pattern.matches(Constants.AGE_RULE, t) }
      .subscribe({
        //inputDataField[2].setText("")
        reactiveInputTextViewData(1, it)
      }) {
        //Error Block
        it.printStackTrace()
      }

//    val compositeDisposable=CompositeDisposable()

    val disposableGender = RxTextView.textChanges(inputDataField[2])
      .map { t -> t.isEmpty() || !Pattern.matches(Constants.GENDER_RULE, t) }
      .subscribe({
        //inputDataField[2].setText("")
        Logg.d("성별 : " + it.toString())
        reactiveInputTextViewData(2, it)
      }) {
        //Error Block
        it.printStackTrace()
      }
    viewDisposables.addAll(
      disposableNick,
      disposableAge,
      disposableGender
    )
  }

  private fun settingEmptyInputUI(indexPath: Int) {
    isInputCorrectData[indexPath] = false
    textInputLayoutArray[indexPath].isErrorEnabled = false
  }

  /**
   * ReActive 로 입력 들어오는 데이터에 대한 결과를 UI 로 표시합니다
   */
  private fun reactiveInputTextViewData(indexPath: Int, it: Boolean) {
    if (inputDataField[indexPath].text.toString().isEmpty().not()) {
      isInputCorrectData[indexPath] = it
    } else {
      isInputCorrectData[indexPath] = false
    }

    textInputLayoutArray[indexPath].error = inputInfoMessage[indexPath]
    textInputLayoutArray[indexPath].isErrorEnabled = !it

    reactiveCheckCorrectData()
  }


  var birth : String? = null
  // intent 결과 받기
  override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
    super.onActivityResult(requestCode, resultCode, intentData)
    // 성별
    if (requestCode == 102 && resultCode == RESULT_OK) {
      editGender.setText(intentData!!.getStringExtra("Gender"))
    }
    // 생년월일 yyyyMMdd 형식으로 전달 받음.
    if (requestCode == 101 && resultCode == RESULT_OK) {
      // 년월일 yyyymmdd로 전달 받음
      Logg.d(intentData!!.getStringExtra("Age"))
      birth = intentData!!.getStringExtra("Age")
      val age = toAge(intentData.getStringExtra("Age")!!)

      editAge.setText(age)
    }

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
        getString(R.string.picture_select_cancel).show()
      }
    }
  }

  /**
   * 올바른 회원정보를 입력 받았는지 체크
   */
  var isSuccess = false

  private fun reactiveCheckCorrectData() {
    for (check in isInputCorrectData) {
      if (!check) {
        isSuccess = false
        Logg.d("입력 상황 : " + check.toString())
        return
      }
    }
    isSuccess = true
  }

  // 바탕 클릭 시 키패드 숨기기
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    editNickname.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    return true
  }

  // 서버로의 회원가입 진행
  private fun signUp(imageUri: Uri, nickname: String, birth: String, gender: String) {
    if (flag == 1) {// false이면 닉네임 체크가 안된 것. 그러면 실행되면 안돼
      Logg.d(birth)
      val timestamp = Date().time
      uploadProfileImage(imageUri, nickname, birth, gender, timestamp.toString())
      uploadUserInfo(nickname, birth, gender, timestamp.toString())
    } else if (flag == 3) {
      textInputLayoutArray[0].setErrorTextColor(resources.getColorStateList(R.color.red, null))
      textInputLayoutArray[0].error = getString(R.string.check_duplicates)
      flag = 3
    }
  }

  var flag = 3 // 닉네임 체크를 위한 flag
  private fun nicknameCheck(nickname: String) {
    // 서버랑 닉네임 체크

    /**
     *  더 좋은 방법이 있을 지 모르지만 whereEqualTo는 db에 없는 것에 대한 검사는 안하는 듯
     *  그래서 flag로 억지로 유무 판단.
     */
    mFirestoreDB!!.collection("userinfo").whereEqualTo("nickname", nickname).get()
      .addOnSuccessListener { documents ->
        for (document in documents) {
          Logg.d(document.id)
          Logg.d(document.exists().toString())
          if (document.exists()) {
            Logg.d("${document.id} => ${document.data}")
            textInputLayoutArray[0].setErrorTextColor(
              resources.getColorStateList(
                R.color.red,
                null
              )
            )
            textInputLayoutArray[0].error = getString(R.string.nickname_already_using)
            flag = 2 // flag = false로 하여 addOnCompleteListener의 if문이 실행 안되게 하기
          }
        }
      }
      .addOnFailureListener { exception ->
        Logg.w("Error getting documents: " + exception)
      }
      .addOnCompleteListener {
        if (flag == 3) {
          textInputLayoutArray[0].setErrorTextColor(
            resources.getColorStateList(
              R.color.yellowGreen,
              null
            )
          )
          textInputLayoutArray[0].error = getString(R.string.nickname_available)

          flag = 1
        }
      }
    flag = 3 // 비동기라서 이건 무조건 실행. 하지만 firebase보단 항상 먼저 실행됨.
  }

  private fun uploadProfileImage(
    imageUri: Uri,
    nickname: String,
    birth: String,
    gender: String,
    timestamp: String
  ) {
    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
    // ...
    UserInfo.autoLoginKey = uid!!
    UserInfo.email = email!!
    UserInfo.nickname = nickname // Shared에 nickname저장.
    UserInfo.birth = birth
    UserInfo.gender = gender

    FBProfileRepository().uploadProfileImage(imageUri, timestamp)

    val nextIntent = Intent(this@SignUpActivity, MainActivity::class.java)
    startActivity(nextIntent)
    progressbar.dismiss()
    finish()
  }

  private fun uploadUserInfo(nickname: String, birth: String, gender: String, dt: String) {
    // 회원 정보
    val data = hashMapOf(
      "UID" to uid,
      "nickname" to nickname,
      "birth" to birth,
      "gender" to gender,
      "profileImagePath" to "Profile/$uid/$dt"
    )
    FBUserInfoRepository().createUserInfo(data)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.backImageBtn -> {
        finish()
      }
      R.id.editAge -> {
        val intent = Intent(this, AgeSelectActivity::class.java)
        startActivityForResult(intent, 101)
      }
      R.id.editGender -> {
        val intent = Intent(this, GenderSelectActivity::class.java)
        startActivityForResult(intent, 102)
        editAge.clearFocus()
        editNickname.clearFocus()
      }
      R.id.profileImage -> {
        // tedPermission()
        checkSelfPermission()
      }
      R.id.redundantCheckButton -> {
        nickname = editNickname.text.toString()
        if (isInputCorrectData[0]) {
          nicknameCheck(nickname!!)
        } else {
          if (!isInputCorrectData[0])
            reactiveInputTextViewData(0, false)
        }

      }
      R.id.sign_up_button -> {

        nickname = editNickname.text.toString()
        age = editAge.text.toString()
        gender = editGender.text.toString()


        Logg.d("가입 버튼 눌렀을 때 : $nickname, $birth, $gender")

        if (textInputLayoutArray[0].error != getString(R.string.nickname_available)) { //무조건 중복 확인 버튼을 눌러야만 회원가입 가능하게 함
          Logg.d(textInputLayoutArray[0].error.toString() + "if문 검사")
          getString(R.string.check_duplicates).show()
        } else { // 중복 확인 통과
//          Logg.d(
//            isInputCorrectData[0].toString() + ", " + age!!.isNotEmpty()
//              .toString() + ", " + gender!!.isNotEmpty().toString()
//          )
          // 단순 editText의 Empty유무 확인 => age는 이곳에서만 쓰이고 안쓰임. -> birth로 나이 관리.
          if (isInputCorrectData[0] && age!!.isNotEmpty() && gender!!.isNotEmpty()) {
            if (selectedImageUri == null) // 프로필 이미지를 설정하지 않았을 때 = 사용자 입장에서 프로필 버튼을 누르지 않았음
            {
              basicProfileSettingPopup() //팝업창으로 물어봄
            } else { // 프로필 이미지 있는 경우 설정한 프로필로 가입하기
              signUp(selectedImageUri!!, nickname!!, birth!!, gender!!)
              progressbar.show() //메인창으로 넘어가기 전까지 프로그래스 바 띄움
            }
          }
          // 정보가 하나라도 입력 안되면 error 메시지 출력
          else {
            for (a in 0..2) {
              // 에러 메시지 중에서도 정보가 입력되것 제외하고 출력.
              if (!isInputCorrectData[a])
                reactiveInputTextViewData(a, false)
            }
          }
        }
      }
    }
  }

  /**
  기본 이미지로 설정할건지 물어보는 팝업
   **/

  lateinit var noticePopup: ChoicePopup

  private fun basicProfileSettingPopup() {
    noticePopup = ChoicePopup(this, getString(R.string.please_select),
      getString(R.string.set_default_image),
      getString(R.string.yes), getString(R.string.no),
      View.OnClickListener {
        //Yes 버튼 눌렀을 때
        //기본 이미지로 설정
        val basicImageUri = Uri.parse(
          "android.resource://" + this.packageName.toString() + "/drawable/basic_profile"
        )
        signUp(basicImageUri!!, nickname!!, birth!!, gender!!)
        noticePopup.dismiss()
        progressbar.show() //기본이미지로 회원가입이 바로 진행되도록 프로그레스바 띄움
      },
      View.OnClickListener {
        // No 버튼 눌렀을 때
        //갤러리에서 원하는 프로필 이미지 선택할 수 있도록 권한체크
//        checkSelfPermission()
        noticePopup.dismiss()
      })
    noticePopup.show()
  }
}