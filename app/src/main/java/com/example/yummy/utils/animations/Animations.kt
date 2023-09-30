package com.example.yummy.utils.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class Animations {


    companion object{


        fun objectAnimator(view: View, float: Float){
            ObjectAnimator.ofFloat(view, "translationX", float).apply {
                duration = 2000
                start()
            }
        }
        fun fadeIn(v: View?) {
            if (v != null) {
                fadeIn(v, null)
            }
        }
//        fun fadeInn(vararg views: View?) {
//            for (view in views) {
//                fadeIn(view!!, null)
//            }
//        }

        private fun fadeIn(v: View, listener: Animator.AnimatorListener?) {
            if (v.visibility != View.VISIBLE) {
                v.visibility = View.VISIBLE
                if (listener != null) {
                    YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .withListener(listener)
                        .playOn(v)
                } else {
                    YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .playOn(v)
                }
            }
        }
    }
}