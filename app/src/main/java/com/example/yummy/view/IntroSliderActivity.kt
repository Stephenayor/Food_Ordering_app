package com.example.yummy.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.yummy.R
import com.example.yummy.databinding.ActivityIntroSliderBinding

import com.google.android.material.tabs.TabLayout


class IntroSliderActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: ActivityIntroSliderBinding

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
//        tabLayout = findViewById(R.id.tabs)

        sliderPagerAdapter = SliderPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        viewPager.adapter = sliderPagerAdapter
//        tabLayout.setupWithViewPager(viewPager)


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
                if (position == -1)
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.purple_700))

                if (position == 0) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.purple_700))

                } else if (position == 1) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.purple_700))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))
                } else {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.purple_700))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.grey))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        }
    }
}