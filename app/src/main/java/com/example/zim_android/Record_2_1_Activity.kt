package com.example.zim_android

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zim_android.databinding.Record21Binding
import java.io.File
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import androidx.exifinterface.media.ExifInterface
import android.util.Size
import android.widget.Toast
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.PreviewView
import com.example.zim_android.Record_3_Activity
import com.example.zim_android.fragment.RecordFragment_1_1
import com.example.zim_android.fragment.RecordFragment_1_2
import java.io.FileOutputStream

class Record_2_1_Activity : AppCompatActivity() {
    private lateinit var binding: Record21Binding
    private lateinit var imageCapture: ImageCapture // 카메라 객체

    private var captureStep = 1 // 몇 번째 촬영인지 담아두는 변수
    private var isFrontCamera: Boolean = true //  기본 초기 세팅은 전면

    // 찍힌 사진 담는 변수
    private var imagePath1: String? = null
    private var imagePath2: String? = null

    // 카운트다운 용 변수
    private var countdownRunnable: Runnable? = null
    private val countdownHandler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Record21Binding.inflate(layoutInflater) // 레이아웃을 binding에 연결
        setContentView(binding.root) // 화면에 뷰 세팅

        binding.backBtnHeader.tvTitle.text = "기록하기"

        binding.backBtnHeader.backBtn.setOnClickListener {
            finish()
        }

        // 카메라 권한 요청
        requestCameraPermissionIfNeeded()

        // 촬영 버튼 클릭
        binding.shutterBtn.setOnClickListener {
            stopCountdownIfRunning()
            takePhoto()
        }

        binding.cameraSwapBtn.setOnClickListener {
            isFrontCamera = !isFrontCamera // 카메라 상태 반전시켜주기
            startCamera(step = captureStep)
        }

        // preview 좌우 반전 시키기
        binding.preview1.scaleX = -1f // 전면으로 시작하니 preview1의 전면만 우선 설정
    }

    // 권한 있는지 확인하고 요청하는 함수
    // 얘는 나중에 온보딩쪽으로 넘어갈 수도 있음.
    private fun requestCameraPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) // manifest의 권한 확인
            == PackageManager.PERMISSION_GRANTED // 허용 시 바로 카메라 시작
        ) {
            startCamera()
        } else { // 거부 시 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }


    // 권한 요청 결과 처리하는 콜백 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera() // 카메라 승인
            } else { // 거부 시 토스트 띄우기
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    // 카메라 시작 함수
    private fun startCamera(step: Int = 1) {
        // CameraX에서 카메라 생명주기를 관리하는 Provider 불러옴
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // 비동기 방식으로 Provider가 준비되면 그걸 가져옴
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 미리보기 해상도 설정
            val targetSize = Size(1041, 957)
            val resolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(targetSize, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER)
                )
                .build()

            // 프리뷰 객체 생성 및 xml의 프리뷰 객체와 연결
            val preview = Preview.Builder().setTargetResolution(targetSize).build().also {
                it.setSurfaceProvider(binding.preview1.surfaceProvider)
            }


            // 촬영된 이미지 객체 생성
            val imageCaptureBuilder = ImageCapture.Builder().setTargetResolution(targetSize) //
            imageCapture = imageCaptureBuilder.build()

            // 카메라 전면 / 후면 설정
            val cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

            // 기존에 연결된 카메라와 연결 헤제
            cameraProvider.unbindAll()

            // 첫 번째 촬영
            if (step == 1) {
                preview.setSurfaceProvider(binding.preview1.surfaceProvider)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } else { // 두 번째 촬영
                binding.overlayImg.setImageResource(R.drawable.black_overlay_transparent_70) // 오버레이 이미지 씌우기
                startCountdownAndTakePhoto() // 카운트다운

                binding.cameraSwapBtn.visibility = View.GONE // 두 번째 촬영에서는 화면 전활 불가능
                preview.setSurfaceProvider(binding.preview2.surfaceProvider)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }
        }, ContextCompat.getMainExecutor(this))
    }



    private fun takePhoto() {
        val file = File(getOutputDirectory(), "${System.currentTimeMillis()}_step${captureStep}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        // 사진을 찍고 저장하는 비동기 과정
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                // 저장된 파일을 bitmap으로 불러오고, 회전/반전 보정을 거침
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    val rotated = rotateAndFlipBitmap(bitmap, file, isFrontCamera)
                    val cropped = centerCropBitmap(rotated, 1041, 957)

                    // 잘린 이미지로 저장
                    FileOutputStream(file).use { out ->
                        cropped.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }

                    // 화면에 표시
                    if (captureStep == 1) {
                        imagePath1 = file.absolutePath  // 1차 촬영 결과 저장
                        binding.imageView1.setImageBitmap(cropped)
                        binding.preview1.visibility = View.GONE

                        isFrontCamera = !isFrontCamera // 카메라 전환
                        binding.preview2.scaleX = if (isFrontCamera) -1f else 1f // 좌우대칭도 맞춰주기

                        captureStep = 2
                        startCamera(step = 2)
                    } else {
                        imagePath2 = file.absolutePath // 2차 촬영 결과 저장

                        binding.imageView2.setImageBitmap(cropped)
                        binding.preview2.visibility = View.GONE

                        if (imagePath1 != null && imagePath2 != null) {
                            // !!! ------- 수정해야할 부분 ------- !!!
                            // 새로운 여행 시작에서 받아온 정보도 같이 넘기기 위해 intent1 사용
                            val intent_add_new = Intent(this@Record_2_1_Activity, RecordFragment_1_2::class.java)
                            // 기존 여행에 추가한 경우, 받아올 정보
                            val intent_add_previout = Intent(this@Record_2_1_Activity, RecordFragment_1_1::class.java)
                            val intent = Intent(this@Record_2_1_Activity, Record_3_Activity::class.java)
                            intent.putExtra("imagePath1", imagePath1)
                            intent.putExtra("imagePath2", imagePath2)
//                            intent.putExtra("trip_name", intent_add_new.getStringExtra("trip_name"))
//                            intent.putExtra("trip_description", intent_add_new.getStringExtra("trip_description"))
//                            intent.putExtra("selected_theme", intent_add_new.getStringExtra("selected_theme"))
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("intent", "intent error")
                        }

//                        // Intent에 두 사진 경로 담아서 넘기기
//                        val intent = Intent(this@Record_2_1_Activity, Record_3_Activity::class.java)
//                        intent.putExtra("imagePath1", imagePath1)
//                        intent.putExtra("imagePath2", imagePath2)
//                        startActivity(intent)
//                        // finish() // 현재 액티비티 종료해서 뒤로가기 막기
                    }
                }

                // 에러 발생 시 로그 출력
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Error: ${exception.message}", exception)
                }
            }
        )
    }





    // 저장 폴더 경로 가져오기
    // 외부 저장소에 저장하고, 없으면 내부 저장소 이용
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "me-mory").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }


    // fil[, rotate 처리 함수
    private fun rotateAndFlipBitmap(bitmap: Bitmap, imageFile: File, isFrontCamera: Boolean): Bitmap {
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()

        // 먼저 회전
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        // 전면일 경우만 좌우 반전
        if (isFrontCamera) {
            matrix.postScale(-1f, 1f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // 이미지 크기 자르는 함수
    private fun centerCropBitmap(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val srcWidth = source.width
        val srcHeight = source.height

        val startX = (srcWidth - targetWidth).coerceAtLeast(0) / 2
        val startY = (srcHeight - targetHeight).coerceAtLeast(0) / 2

        val cropWidth = targetWidth.coerceAtMost(srcWidth)
        val cropHeight = targetHeight.coerceAtMost(srcHeight)

        return Bitmap.createBitmap(source, startX, startY, cropWidth, cropHeight)
    }


    // 권한 요청 시 사용되는 코드 번호
    // requestCode로 비교하기 위한 값
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    // 카운트다운 함수
    private fun startCountdownAndTakePhoto() {
        var count = 15
        val countdownText = binding.countdownText

        countdownText.visibility = View.VISIBLE
        countdownText.text = "${count}s"

        countdownRunnable = object : Runnable {
            override fun run() {
                count--
                if (count > 0) {
                    countdownText.text = "${count}s"
                    countdownHandler.postDelayed(this, 1000)
                } else {
                    countdownText.visibility = View.INVISIBLE
                    takePhoto()
                }
            }
        }
        countdownHandler.postDelayed(countdownRunnable!!, 1000)
    }

    // 카운트 다운 중간에 촬영 셔터 눌렀을 때
    private fun stopCountdownIfRunning() {
        countdownRunnable?.let {
            countdownHandler.removeCallbacks(it)
            binding.countdownText.visibility = View.INVISIBLE
            binding.overlayImg.visibility = View.INVISIBLE
            countdownRunnable = null
        }
    }
}