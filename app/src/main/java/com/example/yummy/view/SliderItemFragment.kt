package com.example.yummy.view

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yummy.R


class SliderItemFragment : Fragment() {

    val ARG_POSITION = "slider-position"
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slider_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var titleText: TextView = view.findViewById(R.id.intro_main_text);
        var subtitleText: TextView = view.findViewById(R.id.intro_sub_text);
        var imageView: ImageView = view.findViewById(R.id.imageView);
        // set page title
        titleText.setText(PAGE_TITLES[position!!]);

        subtitleText.setText(PAGE_TEXT[position!!]);

        imageView.setImageResource(PAGE_IMAGE[position!!]);

        when(position){
            0 ->  imageView.setBackgroundResource(R.drawable.circle_background)

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


    // prepare all title ids arrays
    @StringRes
    private val PAGE_TITLES =
        intArrayOf(R.string.good_food_only, R.string.snacks_and_drinks,
            R.string.get_everything_only)

    // prepare all subtitle ids arrays
    @StringRes
    private val PAGE_TEXT = intArrayOf(
       R.string.order_delicious_food_from_the_best_delivery_n_brand,
        R.string.need_it_now, R.string.order_from
    )

    // prepare all subtitle images arrays
    @StringRes
    private val PAGE_IMAGE = intArrayOf(
        R.drawable.burger, 0, R.drawable.grocery_cart
    )




}