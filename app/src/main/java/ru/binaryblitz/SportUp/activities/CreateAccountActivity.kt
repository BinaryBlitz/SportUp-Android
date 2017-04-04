package ru.binaryblitz.SportUp.activities

import android.Manifest
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activty_create_account.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.CreateUserPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.AndroidUtilities
import javax.inject.Inject


class CreateAccountActivity : BaseActivity() {
    val EXTRA_PHONE = "phone"

    @Inject
    lateinit var api: EndpointsService

    lateinit var dialog: ProgressDialog

    private var base64: String? = null

    private val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom) {
            Thread(Runnable {
                if (bitmap == null) {
                    return@Runnable
                }

                base64 = AndroidUtilities.encodeToBase64(bitmap)
                runOnUiThread({
                    dialog.dismiss()
                    avatar.setImageBitmap(bitmap)
                })
            }).start()
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {

        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_create_account)
        dependencies()!!.inject(this)

        dialog = ProgressDialog(this)

        setOnClickListeners()
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report != null && report.deniedPermissionResponses.size == 0) {
                            openPicker()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    }

                })
                .check()
    }

    private fun loadImage(file: Uri) {
        dialog.show()

        Picasso.with(this).load(file)
                .resize(1200, 1200)
                .centerCrop()
                .into(target)
    }

    private fun openPicker() {
        val tedBottomPicker = TedBottomPicker.Builder(this@CreateAccountActivity)
                .setOnImageSelectedListener { uri ->
                    if (!TextUtils.isEmpty(uri?.path)) {
                        loadImage(uri)
                    }
                }
                .create()

        tedBottomPicker.show(supportFragmentManager)
    }

    fun onLoaded() {
        dialog.dismiss()
    }

    private fun setOnClickListeners() {
        send.setOnClickListener {
            createUser()
        }

        avatar.setOnClickListener {
            checkPermissions()
        }
    }

    private fun generateJson(): JsonObject {
        val obj = JsonObject()

        obj.addProperty("first_name", firstName.text.toString())
        obj.addProperty("last_name",lastName.text.toString() )
        obj.addProperty("phone_number", intent.getStringExtra(EXTRA_PHONE))
        obj.addProperty("avatar", "")

        val toSend = JsonObject()

        toSend.add("user", obj)

        return toSend
    }

    private fun createUser() {
        dialog.show()
        val presenter = CreateUserPresenter(api, this)
        presenter.createUser(generateJson())
    }
}