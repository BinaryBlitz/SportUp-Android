package ru.binaryblitz.SportUp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.JsonElement
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.io.ByteArrayOutputStream

@SuppressWarnings("unused")
object AndroidUtilities {

    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    fun hideKeyboard(v: View) {
        val imm = v.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun encodeToBase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun nameEqualsTo(item: String?, query: String): Boolean {
        if (item == null) {
            return false
        }
        return item.toLowerCase().contains(query)
    }

    fun dpToPx(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun colorAndroidBar(context: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = context.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    fun validatePhone(phNumber: String): Boolean {
        if (phNumber.isEmpty()) return false

        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt("+7"))
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            LogUtil.logException(e)
            return false
        }

        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    fun validateEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun processText(phone: EditText): String {
        var text = phone.text.toString()
        text = text.replace("(", "")
        text = text.replace(")", "")
        text = text.replace("-", "")
        text = text.replace(" ", "")

        return text
    }

    fun call(context: Context, phone: String) {
        val call = Uri.parse("tel:" + phone)
        val intent = Intent(Intent.ACTION_DIAL, call)
        context.startActivity(intent)
    }

    fun getStringFieldFromJson(element: JsonElement?): String {
        if (element == null || element.isJsonNull) {
            return ""
        } else {
            return element.asString
        }
    }

    fun getUrlFieldFromJson(element: JsonElement?): String {
        if (element == null || element.isJsonNull) {
            return "null"
        } else {
            return element.asString
        }
    }

    fun getIntFieldFromJson(element: JsonElement?): Int {
        if (element == null || element.isJsonNull) {
            return 0
        } else {
            return element.asInt
        }
    }

    fun getDoubleFieldFromJson(element: JsonElement?): Double {
        if (element == null || element.isJsonNull) {
            return 0.0
        } else {
            return element.asDouble
        }
    }

    fun getBooleanFieldFromJson(element: JsonElement?): Boolean {
        if (element == null || element.isJsonNull) {
            return false
        } else {
            return element.asBoolean
        }
    }

    fun checkPlayServices(context: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                context.finish()
            }
            return false
        }
        return true
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}
