package com.example.zim_android.View

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.Adapter.OnBoardingAdapter
import com.example.zim_android.MainActivity
import com.example.zim_android.PreferenceManager
import com.example.zim_android.R
import com.example.zim_android.databinding.ActivityOnboardingBinding
class OnBoardingActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnBoardingAdapter
    private var selectedImageView: ImageView? = null

    private val pageImages = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4,
        R.drawable.onboarding_5,
        R.drawable.onboarding_6,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = OnBoardingAdapter(pageImages)
        binding.onboardingViewPager.adapter = adapter

        setupIndicator()
        setCurrentIndicator(0)

        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)

                when (position) {
                    in 0..2 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.next_big_but)
                    }
                    3 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.kakao_but)
                    }
                    4 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.next_big_but_non)

                        binding.onboardingViewPager.post {
                            setupPhotoPicker()
                            observeInputFields()
                        }
                    }

                    5 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.start_big_but)
                    }
                }
            }
        })

        binding.nextButton.setOnClickListener {
            val nextIndex = binding.onboardingViewPager.currentItem + 1
            if (nextIndex < adapter.itemCount) {
                binding.onboardingViewPager.currentItem = nextIndex
            } else {
                PreferenceManager.setOnboardingShown(this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupPhotoPicker() {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

        val photoUploadBox = currentView.findViewById<ImageView>(R.id.photoUploadBox)
        photoUploadBox.setOnClickListener {
            selectedImageView = photoUploadBox
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            selectedImageView?.setImageURI(imageUri)
        }
    }

    private fun observeInputFields() {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

        val koreanNameEdit = currentView.findViewById<EditText>(R.id.koreanNameEdit)
        val birthdayEdit = currentView.findViewById<EditText>(R.id.birthdayEdit)
        val lastNameEdit = currentView.findViewById<EditText>(R.id.lastNameEngEdit)
        val firstNameEdit = currentView.findViewById<EditText>(R.id.firstNameEngEdit)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updatePage4ButtonState(koreanNameEdit, birthdayEdit, lastNameEdit, firstNameEdit)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        koreanNameEdit.addTextChangedListener(watcher)
        birthdayEdit.addTextChangedListener(watcher)
        lastNameEdit.addTextChangedListener(watcher)
        firstNameEdit.addTextChangedListener(watcher)
    }

    private fun updatePage4ButtonState(
        koreanEdit: EditText,
        birthEdit: EditText,
        lastEdit: EditText,
        firstEdit: EditText
    ) {
        val allFilled = listOf(
            koreanEdit.text?.isNotBlank(),
            birthEdit.text?.isNotBlank(),
            lastEdit.text?.isNotBlank(),
            firstEdit.text?.isNotBlank()
        ).all { it == true }

        binding.nextButton.setBackgroundResource(
            if (allFilled) R.drawable.next_big_but else R.drawable.next_big_but_non
        )
    }

    private fun setupIndicator() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layout = binding.indicatorLayout
        layout.removeAllViews()

        for (i in indicators.indices) {
            indicators[i] = ImageView(this).apply {
                setImageResource(R.drawable.dot_inactive)
                val params = LinearLayout.LayoutParams(24, 24)
                params.setMargins(8, 0, 8, 0)
                layout.addView(this, params)
            }
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val layout = binding.indicatorLayout
        for (i in 0 until layout.childCount) {
            val imageView = layout.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageResource(R.drawable.icon_plane)
                val width = (27 * resources.displayMetrics.density).toInt()
                val height = (26 * resources.displayMetrics.density).toInt()
                imageView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
                    setMargins(8, 0, 8, 0)
                }
            } else {
                imageView.setImageResource(R.drawable.dot_inactive)
                val size = (10 * resources.displayMetrics.density).toInt()
                imageView.layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(12, 0, 12, 0)
                }
            }
        }
    }
}
