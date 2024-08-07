package com.example.yummy.core.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.yummy.R
import com.example.yummy.databinding.ActivityIntroSliderBinding
import com.example.yummy.core.onboarding.activity.OnboardingActivity


class IntroSliderActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private lateinit var binding: ActivityIntroSliderBinding
    private var runnable: Runnable? = null
    private lateinit var navController: NavController
    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        indicatorSlideOneTV = binding.idTVSlideOne
        indicatorSlideTwoTV = binding.idTVSlideTwo
        indicatorSlideThreeTV = binding.idTVSlideThree

        supportActionBar?.hide()
        viewPager = findViewById(R.id.pagerIntroSlider)
        sliderPagerAdapter = SliderPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        viewPager.adapter = sliderPagerAdapter


        /**
         * Add a listener that will be invoked whenever the page changes
         * or is incrementally scrolled
         */
        /**
         * Add a listener that will be invoked whenever the page changes
         * or is incrementally scrolled
         */
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    -1, 0 -> {
                        indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                        indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                        indicatorSlideOneTV.setTextColor(resources.getColor(R.color.purple_700))
                    }

                    1 -> {
                        indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.purple_700))
                        indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                        indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))
                    }

                    else -> {
                        indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                        indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.purple_700))
                        indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        startAutoSlider(3).observe(this) { pos ->
            viewPager.currentItem = pos
        }
        setupClickListeners()
    }

    private fun setupClickListeners()  {
        binding.btnSignUp.setOnClickListener {
            OnboardingActivity.start(this, false)
        }
        binding.btnLogin.setOnClickListener {
            OnboardingActivity.start(this, true)
        }
    }

    private fun startAutoSlider(count: Int): LiveData<Int> {
        val resultLiveData = MutableLiveData<Int>()
        val handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            var pos: Int = viewPager.currentItem
            pos += 1
            if (pos >= count) pos = 0
            viewPager.currentItem = pos
            resultLiveData.value = pos
            handler.postDelayed(runnable!!, 3000)
        }
        handler.postDelayed(runnable!!, 3000)
        return resultLiveData
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(
                context,
                IntroSliderActivity::class.java
            )
            context.startActivity(starter)
        }
    }
}