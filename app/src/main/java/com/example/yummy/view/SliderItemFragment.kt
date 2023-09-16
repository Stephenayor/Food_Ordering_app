package com.example.yummy.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.yummy.R
import com.example.yummy.databinding.ActivityIntroSliderBinding
import com.example.yummy.databinding.FragmentSliderItemBinding
import com.google.android.material.tabs.TabLayout


class SliderItemFragment : Fragment() {
    val ARG_POSITION = "slider-position"
    private var position: Int? = null
    private lateinit var binding: FragmentSliderItemBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = requireArguments().getInt(ARG_POSITION);
        }
    }

    fun newInstance(position: Int): SliderItemFragment {
        val fragment = SliderItemFragment()
        val args = Bundle()
        args.putInt(ARG_POSITION, position)
        fragment.arguments = args
        return fragment
    }

    fun navigate() {
        val action = SliderItemFragmentDirections.actionSliderItemFragmentToSignUpFragment4()
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSliderItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleText: TextView = binding.introMainText
        val subtitleText: TextView = binding.introSubText
        val imageView: ImageView = view.findViewById(R.id.imageView);



        // set page title
//        binding.introMainText.setText(PAGE_TITLES[position])
//
//        subtitleText.setText(PAGE_TEXT[position!!]);

        //        imageView.setImageResource(PAGE_IMAGE[0]);


        setupSliderItemsTitle(titleText)

        setupSliderImages(imageView, PAGE_IMAGE)

        setupSliderSubText(subtitleText)

        when (position) {
            0 -> imageView.setBackgroundResource(R.drawable.circle_background)

            1 -> {
                imageView.setBackgroundResource(R.drawable.circle_background)
                val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.food)
                val newWidth = 150
                val newHeight = 150

                val resizedBitmap =
                    Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)
                imageView.setImageBitmap(resizedBitmap)
            }

            2 -> {
                imageView.setBackgroundResource(R.drawable.circle_background_design_secondary)
            }
        }


    }



    private fun setupSliderSubText(subtitleText: TextView) {
        when (position) {
            0 -> subtitleText.setText(PAGE_TEXT[position!!])

            1 -> {
                subtitleText.setText(PAGE_TEXT[position!!])
            }

            2 -> {
                subtitleText.setText(PAGE_TEXT[position!!])
            }
        }
    }

    private fun setupSliderImages(imageView: ImageView, PAGE_IMAGE: IntArray) {
        when (position) {
            0 -> imageView.setImageResource(PAGE_IMAGE[position!!])

            1 -> {

            }

            2 -> {
                imageView.setImageResource(PAGE_IMAGE[position!!])
            }
        }
    }

    private fun setupSliderItemsTitle(titleText: TextView) {
        when (position) {
            0 -> titleText.setText(R.string.good_food_only)

            1 -> titleText.setText(R.string.snacks_and_drinks)

            2 -> titleText.setText(R.string.get_everything_only)
        }
    }


    // prepare all title ids arrays
    @StringRes
    private val PAGE_TITLES =
        intArrayOf(
            R.string.good_food_only, R.string.snacks_and_drinks,
            R.string.get_everything_only
        )

    // prepare all subtitle ids arrays
    @StringRes
    private val PAGE_TEXT = intArrayOf(
        R.string.order_delicious_food_from_the_best_delivery_n_brand,
        R.string.need_it_now, R.string.order_from
    )

    // prepare all subtitle images arrays
    @StringRes
    val PAGE_IMAGE = intArrayOf(
        R.drawable.burger, 0, R.drawable.grocery_cart
    )

    companion object{

    }
}