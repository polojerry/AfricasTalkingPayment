package com.polotechnologies.africastalkingpayment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.africastalking.AfricasTalking
import com.africastalking.PaymentService
import com.africastalking.payment.response.CheckoutResponse
import com.polotechnologies.africastalkingpayment.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var payment: PaymentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeSdk()

        payment = AfricasTalking.getService(AfricasTalking.SERVICE_PAYMENT)

        binding.buttonStartPayment.setOnClickListener {
            if (validateInput()) {
                startCheckOutProcess()
            }
        }
    }


    private fun initializeSdk() {
        val USER_NAME = "sandbox"
        val API_KEY = "YOUR_API_KEY"

        AfricasTalking.initialize(USER_NAME, API_KEY)

    }

    private fun validateInput(): Boolean {
        val isValid: Boolean

        if (binding.textUserNumber.text.isNullOrEmpty()) {
            binding.layoutAccountNumber.error = "Phone Number Required"
            isValid = false
        } else {
            isValid = true
        }

        return isValid
    }

    private fun startCheckOutProcess() {

        val productName = "Apewe Fish"

        val phoneNumber = "+254790689212"

        val currencyCode = "KES"

        val amount = 100f

        val metadata =
            HashMap<String, String>()
        metadata["someKey"] = "someValue"

        val providerChannel = "11001"

        try {
            val response: CheckoutResponse = payment.mobileCheckout(
                productName, phoneNumber, currencyCode, amount, metadata, providerChannel
            )

            val jsonObject = JSONObject(response.toString())

            if (jsonObject.getString("status") == "PendingConfirmation") {

                Toast.makeText(this, "Success, pending confirmation", Toast.LENGTH_SHORT).show()
                Log.d("PAYMENT RESPONSE", response.toString())

            } else {

                Toast.makeText(
                    this,
                    "Failed: ${jsonObject.getString("description")}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("PAYMENT RESPONSE", response.toString())

            }

        } catch (ex: Exception) {
            Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }
}