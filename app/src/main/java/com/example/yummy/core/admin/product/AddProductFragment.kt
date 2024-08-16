package com.example.yummy.core.admin.product

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.yummy.R
import com.example.yummy.core.admin.AdminActivity
import com.example.yummy.databinding.FragmentAddProductBinding
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.NavigateTo
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.imagepicker.ImagePickerHelper
import dagger.hilt.android.AndroidEntryPoint
import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import java.io.File

@AndroidEntryPoint
class AddProductFragment : BaseFragment<FragmentAddProductBinding>() {
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentAddProductBinding
    private var imagePickerHelper: ImagePickerHelper? = null
    private val PERMISSION_REQUEST_CODE = 1001
    private val PICK_IMAGE_REQUEST_CODE = 1
    private lateinit var imageView: ImageView
    private var imageUriOnFirebaseStorage: Uri? = null
    private val addProductViewModel by viewModels<AddProductViewModel>()
    override fun getLayoutId(): Int = R.layout.fragment_add_product

    override fun getLayoutBinding(binding: FragmentAddProductBinding) {
        this.binding = binding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = binding.selectedImageView

        setClickListeners()
        subscribeToLiveData()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton()
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private val priceTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton()
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private fun subscribeToLiveData() {
        addProductViewModel.cloudStorageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    hideLoading()
                    binding.progressCircular.visibility = View.GONE
                    Tools.showToast(requireContext(), "Image Upload Successful")
                    imageUriOnFirebaseStorage = response.data
                    enableDisableButton()
                }

                is Resource.Error -> {
                    hideLoading()
                    binding.progressCircular.visibility = View.GONE
                    Tools.showToast(requireContext(), "Error Uploading Image to Cloud Storage")
                }

                else -> {}
            }
        }

        addProductViewModel.addProductToFireStoreResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.uploading_products, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    if (response.data == true) {
                        val action =
                            AddProductFragmentDirections.actionAddProductFragmentToSuccessFragment3(
                                AppConstants.REQUEST_SUCCESSFUL,
                                "Products have been added successfully",
                                AppConstants.CONTINUE,
                                NavigateTo.ADMIN_DASHBOARD.toString()
                            )
                        findNavController().navigate(action)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }

                is Resource.Error -> {
                    hideLoading()
                    Tools.openSuccessErrorDialog(
                        requireActivity(),
                        response.message,
                        "Failed",
                        false,
                        false
                    )
                }

                else -> {}
            }
        }
    }


    private fun setClickListeners() {
        binding.addImageView.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnPublishProducts.setOnClickListener {

            addProductViewModel.uploadProductsOnFireStore(
                imageUriOnFirebaseStorage!!, binding.productNameEditTextField.text.toString(),
                binding.productPriceEditTextField.text.toString().toDouble()
            )
        }
//            Tools.showToast(requireContext(), "Please Fill All Details/ReUpload Selected Image")


        binding.productNameEditTextField.addTextChangedListener(textWatcher)
        binding.productPriceEditTextField.addTextChangedListener(priceTextWatcher)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            val filePath = data?.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
            val selectedImage = BitmapFactory.decodeFile(filePath)
            imageView.setImageBitmap(selectedImage)
            binding.addImageView.visibility = View.GONE
            val fileUri = Uri.fromFile(filePath?.let { File(it) })
            Log.d("URI", fileUri.toString())
            uploadImageToCloud(fileUri)
        }
    }

    private fun uploadImageToCloud(image: Uri) {
        imageUri = image
        addProductViewModel.uploadImageToCloudStorage(image)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(requireActivity(), ImageSelectActivity::class.java)
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false)
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true)
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true)
        intent.putExtra(ImageSelectActivity.FLAG_CROP, false)
        startActivityForResult(intent, 1213)
    }

    private fun enableDisableButton() {
        binding.btnPublishProducts.isEnabled =
            binding.productNameEditTextField.text?.isNotEmpty() == true &&
                    binding.productPriceEditTextField.text?.isNotEmpty() == true
                    && imageUriOnFirebaseStorage != null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddProductFragment().apply {

            }
    }
}