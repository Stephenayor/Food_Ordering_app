package com.example.yummy.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.ceryle.radiorealbutton.RadioRealButton
import com.example.yummy.R
import com.example.yummy.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {
    private var realButtonAdmin: RadioRealButton? = null
    private var realButtonPersonal:RadioRealButton? = null
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realButtonAdmin = binding.btnAdmin
        realButtonPersonal = binding.btnPersonal
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {

            }
    }
}