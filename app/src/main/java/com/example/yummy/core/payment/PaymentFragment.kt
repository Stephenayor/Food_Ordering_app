package com.example.yummy.core.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.example.yummy.R
import com.example.yummy.databinding.FragmentPaymentBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.formatter.CreditCardTextFormatter
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import java.util.Calendar

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {
    private lateinit var binding: FragmentPaymentBinding
    private var transaction: Transaction? = null
    private var charge: Charge? = null
    private lateinit var cardNumberText: EditText
    private lateinit var expiryDate: EditText
    private lateinit var cvvText: EditText
    private val paymentFragmentArgs by navArgs<PaymentFragmentArgs>()
    private var totalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_payment

    override fun getLayoutBinding(binding: FragmentPaymentBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardNumberText = binding.etCardNumber
        expiryDate = binding.etExpiry
        cvvText = binding.etCvv
        initViews()
    }

    private fun initViews() {
        addTextWatcherToEditText()
        totalPrice = paymentFragmentArgs.totalAmount
        val payText = "Pay " + Tools.formatToCommaNaira(requireContext(), totalPrice.toString())
        binding.btnPay.text = payText

        handleClicks()
    }


    /**
     * Add formatting to card number, cvv, and expiry date
     */

    private fun addTextWatcherToEditText() {
        //Make button UnClickable for the first time
        binding.btnPay.isEnabled = false
        binding.btnPay.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_round_opaque)

        //make the button clickable after detecting changes in input field
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val s1 = binding.etCardNumber.text.toString()
                val s2 = binding.etExpiry.text.toString()
                val s3 = binding.etCvv.text.toString()

                //check if they are empty, make button unclickable
                if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty()) {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_round_opaque
                    )
                }


                if (s2.length == 5) {
                    binding.etCvv.requestFocus()
                }

                //check the length of all edit text, if meet the required length, make button clickable
                if (s1.length >= 16 && s2.length == 5 && s3.length == 3) {
                    binding.btnPay.isEnabled = true
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_border_blue_bg
                    )
                }

                //if edit text doesn't meet the required length, make button unclickable. You could use else statement from the above if
                if (s1.length < 16 || s2.length < 5 || s3.length < 3 || s1.length == 16) {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_round_opaque
                    )
                }

                //add a slash to expiry date after first two character(month)
                if (s2.length == 2) {
                    if (start == 2 && before == 1 && !s2.contains("/")) {
                        binding.etExpiry.setText(
                            requireActivity().getString(
                                R.string.expiry_space,
                                s2[0]
                            )
                        );
                        binding.etExpiry.setSelection(1)
                    } else {
                        binding.etExpiry.setText(
                            requireActivity().getString(
                                R.string.expiry_slash,
                                s2
                            )
                        );
                        binding.etExpiry.setSelection(3)
                    }
                }


            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        //add text watcher
        binding.etCardNumber.addTextChangedListener(CreditCardTextFormatter())
        binding.etCardNumber.addTextChangedListener(watcher)
        binding.etExpiry.addTextChangedListener(watcher)
        binding.etCvv.addTextChangedListener(watcher)


    }

    private fun handleClicks() {
        //on click of pay button
        binding.btnPay.setOnClickListener {
            binding.loadingPayOrder.visibility = View.VISIBLE
            if (enableDisableButton()) {
                //show loading
                binding.btnPay.visibility = View.GONE
                //perform payment
                doPayment()
            } else {
                binding.loadingPayOrder.visibility = View.GONE
                Tools.showToast(requireContext(), "Please Check your Card Details")
            }

        }
    }

    private fun enableDisableButton(): Boolean {
        return cardNumberText.text.isNotEmpty() &&
                expiryDate.text.isNotEmpty()
                && cvvText.text.isNotEmpty()
    }

    private fun doPayment() {
        val publicTestKey = "pk_test_d36f30c7e99092576e56090ab264e449f14778eb"
        PaystackSdk.setPublicKey(publicTestKey)
        // Initialize the charge
        charge = Charge()
        charge!!.card = loadCardFromForm()

        charge!!.amount = totalPrice * 100
        charge!!.email = "email@gmail.com"
        charge!!.reference = "payment" + Calendar.getInstance().timeInMillis

        try {
            charge!!.putCustomField("Charged From", "Android SDK")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        doChargeCard()
    }


    private fun loadCardFromForm(): Card {

        //Validate fields
        val cardNumber = binding.etCardNumber.text.toString().trim()
        val expiryDate = binding.etExpiry.text.toString().trim()
        val cvc = binding.etCvv.text.toString().trim()

        //Formatted values
        val cardNumberWithoutSpace = cardNumber.replace(Regex("[^0-9]"), "")
        val monthValue = expiryDate.substring(0, expiryDate.length.coerceAtMost(2))
        val yearValue = expiryDate.takeLast(2)

        //Build card object with ONLY the number, update the other fields later
        val card: Card = Card.Builder(cardNumberWithoutSpace, 0, 0, "").build()

        //Update the cvc field of the card
        card.cvc = cvc

        //validate expiry month;
        val sMonth: String = monthValue
        var month = 0
        try {
            month = sMonth.toInt()
        } catch (ignored: Exception) {
        }

        card.expiryMonth = month

        //validate expiry year
        val sYear: String = yearValue
        var year = 0
        try {
            year = sYear.toInt()
        } catch (ignored: Exception) {
        }
        card.expiryYear = year

        return card

    }


    /**
     * DO charge and receive call backs - successful and failed payment
     */

    private fun doChargeCard() {

        transaction = null
        PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {

            // This is called only after transaction is successful
            override fun onSuccess(transaction: Transaction) {
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility = View.VISIBLE

                //successful, show a toast or navigate to another activity or fragment
                Toast.makeText(
                    requireActivity(),
                    "Yeeeee!!, Payment was successful",
                    Toast.LENGTH_LONG
                ).show()

                this@PaymentFragment.transaction = transaction
                //now you can store the transaction reference, and perform a verification on your backend server
            }

            override fun beforeValidate(transaction: Transaction) {
                this@PaymentFragment.transaction = transaction
            }

            override fun onError(error: Throwable, transaction: Transaction) {
                //stop loading
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility = View.VISIBLE

                if (transaction.reference != null) {
                    Toast.makeText(requireActivity(), error.message ?: "", Toast.LENGTH_LONG).show()
                    //also you can do a verification on your backend server here
                } else {
                    Toast.makeText(requireActivity(), error.message ?: "", Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    override fun onPause() {
        super.onPause()
        binding.loadingPayOrder.visibility = View.GONE
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            PaymentFragment().apply {

            }
    }
}