package ru.binaryblitz.SportUp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.gson.JsonObject
import com.nineoldandroids.animation.Animator
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_registration.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.RegistrationPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import ru.binaryblitz.SportUp.utils.AnimationStartListener
import ru.binaryblitz.SportUp.utils.CustomPhoneNumberTextWatcher
import ru.binaryblitz.SportUp.utils.LogUtil
import javax.inject.Inject

class RegistrationActivity : BaseActivity() {
    private var code = false
    private var phoneEditText: MaterialEditText? = null
    private var codeEditText: MaterialEditText? = null
    private var continueButton: Button? = null
    val EXTRA_PHONE = "phone"

    @Inject
    lateinit var api: EndpointsService

    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        dependencies()!!.inject(this)

        initElements()
        setOnClickListeners()

        dialog = ProgressDialog(this)

        Handler().post { phoneEditText!!.requestFocus() }
    }

    override fun onBackPressed() {
        if (!code)
            super.onBackPressed()
        else {
            animateBackBtn()
            resetFields()
        }
    }

    private fun initElements() {
        continueButton = findViewById(R.id.button) as Button
        phoneEditText = findViewById(R.id.phone) as MaterialEditText
        codeEditText = findViewById(R.id.code_field) as MaterialEditText
        phoneEditText?.addTextChangedListener(CustomPhoneNumberTextWatcher())

        codeEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 5) {
                    verifyRequest()
                }
            }
        })
    }

    private fun animateBackBtn() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(ANIMATION_DURATION.toLong())
                .withListener(object : AnimationStartListener() {
                    override fun onStart() {
                        phoneLayout.visibility = View.VISIBLE

                        YoYo.with(Techniques.SlideInLeft)
                                .duration(ANIMATION_DURATION.toLong())
                                .playOn(phoneLayout)

                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(phoneEditText, InputMethodManager.SHOW_IMPLICIT)
                    }
                })
                .playOn(codeLayout)
    }

    private fun resetFields() {
        codeEditText?.setText("")
        continueButton?.visibility = View.VISIBLE
        helperText.text = getString(R.string.code_send)
        title_text.text = getString(R.string.type_phone)
        code = false
    }

    private fun processPhoneInput() {
        val phoneText = phoneEditText?.text.toString()
        if (phoneText.isEmpty()) {
            phoneEditText?.error = getString(R.string.empty_field)
            return
        }

        auth()
    }

    fun dismissProgress() {
        dialog.dismiss()
    }

    private fun setOnClickListeners() {
        continueButton?.setOnClickListener { v ->
            AndroidUtilities.hideKeyboard(v)
            processPhoneInput()
        }

        left_btn.setOnClickListener {
            if (code) {
                animateBackBtn()
                resetFields()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun checkCodeInput(): Boolean {
        if (codeEditText?.text.toString().isEmpty() || codeEditText?.text.toString().length != 5) {
            codeEditText?.error = getString(R.string.wrong_code)
            return false
        }

        return true
    }

    private fun verifyRequest() {
        if (!checkCodeInput()) {
            return
        }
        executeVerifyRequest()
    }

    private fun saveInfo(obj: JsonObject) {
        dialog.dismiss()
        val phone = phoneEditText?.text.toString()
        savePhone(phone)
        saveToken(obj)
        continueToNextScreen(obj.get("api_token") != null && !obj.get("api_token").isJsonNull)
    }

    private fun continueToNextScreen(isAccountCreated: Boolean) {
        if (isAccountCreated) {
            openActivity(MainActivity::class.java)
        } else {
            openActivity(CreateAccountActivity::class.java)
        }
    }

    private fun openActivity(activity: Class<out Activity>) {
        val intent = Intent(this, activity)
        intent.putExtra(EXTRA_PHONE, phoneFromServer)
        startActivity(intent)
        finish()
    }

    private fun saveToken(obj: JsonObject) {
        val token = obj.get("api_token")
        if (!token.isJsonNull) {
            DeviceInfoStore.saveToken(this, token.asString)
        }
    }

    private fun executeVerifyRequest() {
        val presenter = RegistrationPresenter(api, this)

        dialog.show()

        presenter.verify(generateVerifyJson(), token!!)
    }

    private fun generateVerifyJson(): JsonObject {
        val obj = JsonObject()

        obj.addProperty("token", token)
        obj.addProperty("code", codeEditText?.text.toString())

        return obj
    }

    fun showCodeError() {
        Snackbar.make(main, R.string.wrong_code, Snackbar.LENGTH_SHORT).show()
    }

    fun onVerifyAnswer(obj: JsonObject) {
        saveInfo(obj)
    }

    private fun savePhone(phone: String) {
        val user = DeviceInfoStore.getUserObject(this) ?: return
        user.phone = phone
        DeviceInfoStore.saveUser(this, user)
    }

    private fun auth() {
        val presenter = RegistrationPresenter(api, this)

        dialog.show()

        presenter.auth(processText())
    }

    fun onAuthResponse(body: JsonObject) {
        parseAuthRequestAnswer(body)
        playOutAnimation(phoneLayout, timer_text)
    }

    private fun parseAuthRequestAnswer(obj: JsonObject) {
        dialog.dismiss()
        code = true
        token = obj.get("token").asString
        phoneFromServer = obj.get("phone_number").asString
        continueButton?.visibility = View.GONE
    }

    private fun processText(): JsonObject {
        var phoneNext = phoneEditText?.text.toString()
        phoneNext = phoneNext.replace("(", "")
        phoneNext = phoneNext.replace(")", "")
        phoneNext = phoneNext.replace("-", "")
        phoneNext = phoneNext.replace(" ", "")

        val obj = JsonObject()
        obj.addProperty("phone_number", phoneNext)

        return obj
    }

    private fun playOutAnimation(firstView: View, secondView: View) {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(ANIMATION_DURATION.toLong())
                .withListener(object : AnimationStartListener() {
                    override fun onStart() {
                        findViewById(R.id.codeLayout).visibility = View.VISIBLE
                        YoYo.with(Techniques.SlideInRight)
                                .duration(ANIMATION_DURATION.toLong())
                                .playOn(findViewById(R.id.codeLayout))
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        codeEditText!!.requestFocus()
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    }
                })
                .playOn(firstView)

        YoYo.with(Techniques.SlideOutLeft)
                .duration(ANIMATION_DURATION.toLong())
                .withListener(object : AnimationStartListener() {
                    override fun onStart() {
                        helperText.text =
                                getString(R.string.number_code) + " " + phoneEditText?.text.toString() + getString(R.string.code_sent)

                        title_text.text = getString(R.string.code_title)
                    }
                })
                .playOn(secondView)
    }

    companion object {
        private val ANIMATION_DURATION = 700
        private var token: String? = ""
        private var phoneFromServer: String? = null
    }
}
