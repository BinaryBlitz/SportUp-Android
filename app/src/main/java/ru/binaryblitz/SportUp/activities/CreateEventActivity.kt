package ru.binaryblitz.SportUp.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_create_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.presenters.CreateEventPresenter
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.LogUtil
import ru.binaryblitz.SportUp.utils.SportTypesUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateEventActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    var isStartTimePicked = true

    val USER_LIMIT = 1
    val SPORT_TYPE = 2
    val TEAM_LIMIT = 3

    val EXTRA_ID = "id"
    val EXTRA_COLOR = "color"

    var startDate: Date? = null
    var endDate: Date? = null
    val calendar: Calendar = Calendar.getInstance()
    var sportTypeId = 0

    var errorString = ""
    var error = false

    lateinit var dialog: ProgressDialog

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        dependencies()!!.inject(this)

        try {
            SportTypesUtil.load(this)
        } catch (e: Exception) {
            LogUtil.logException(e)
        }

        setOnClickListeners()
    }

    private fun initFormat(): SimpleDateFormat {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")

        return format
    }

    fun onLoaded(id: Int, color: Int) {
        dialog.dismiss()
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        intent.putExtra(EXTRA_COLOR, color)
        startActivity(intent)
        finish()
    }

    private fun showPriceDialog() {
        MaterialDialog.Builder(this)
            .title(getString(R.string.event_price))
            .inputType(InputType.TYPE_CLASS_NUMBER)
            .input(getString(R.string.input_hint), "") { _, input ->
                priceText.text = input.toString() + getString(R.string.ruble_sign)
            }.show()
    }

    private fun checkInputs() {
        checkCondition(nameEdit.text.toString().isEmpty(), R.string.event_name_error)
        checkCondition(latLng == null, R.string.wrong_event_location)
        checkCondition(!DateUtils.isAfterToday(startDate), R.string.wrong_event_date)
        checkCondition(!DateUtils.isAfter(startDate, endDate), R.string.wrong_event_end_date)
        checkCondition(userLimitValue.text.toString() == "0", R.string.wrong_event_user_limit)
        checkCondition(teamLimitValue.text.toString() == "0", R.string.wrong_event_team_limit)
        checkCondition(sportTypeId == 0, R.string.wrong_event_sport_type)
    }

    private fun checkCondition(condition: Boolean, errorStringId: Int) {
        if (condition) {
            errorString += getString(errorStringId) + "\n"
            error = true
        }
    }

    private fun generateJson(): JsonObject {
        val event = JsonObject()
        val obj = JsonObject()

        val format = initFormat()

        event.addProperty("name", nameEdit.text.toString())
        event.addProperty("starts_at", format.format(startDate))
        event.addProperty("ends_at", format.format(endDate))
        event.addProperty("address", locationText.text.toString())
        event.addProperty("latitude", 0.0)
        event.addProperty("longitude", 0.0)
        event.addProperty("user_limit", userLimitValue.text.toString())
        event.addProperty("team_limit", teamLimitValue.text.toString())
        event.addProperty("description", descriptionEdit.text.toString())
        event.addProperty("price", priceText.text.toString().split(" ")[0])
        event.addProperty("sport_type_id", sportTypeId)
        event.addProperty("public", isPublicSwitch.isChecked)

        obj.add("event", event)

        return obj
    }

    private fun sendEvent() {
        checkInputs()

        if (error) {
            showErrorDialog()
            return
        }

        dialog = ProgressDialog(this)
        dialog.show()

        val presenter = CreateEventPresenter(api, this)
        presenter.createEvent(generateJson(), "foobar")
    }

    private fun showErrorDialog() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.error))
                .content(errorString)
                .positiveText(getString(R.string.ok))
                .show()
    }

    private fun setOnClickListeners() {
        rightBtn.setOnClickListener { sendEvent() }

        backBtn.setOnClickListener { finish() }

        dateStart.setOnClickListener { showDatePicker() }

        priceButton.setOnClickListener { showPriceDialog() }

        timeStart.setOnClickListener {
            isStartTimePicked = true
            showTimePicker()
        }

        endTime.setOnClickListener {
            isStartTimePicked = false
            showTimePicker()
        }

        userLimitButton.setOnClickListener {
            showNumberPicker(getString(R.string.players_quantity), USER_LIMIT)
        }

        teamLimitButton.setOnClickListener {
            showNumberPicker(getString(R.string.teams_quantity), TEAM_LIMIT)
        }

        selectTypeButton.setOnClickListener {
            showNumberPicker(getString(R.string.select_sport), SPORT_TYPE)
        }
    }

    private fun showNumberPicker(title: String, field: Int) {
        val picker = buildNumberPickerDialog(field)
        showNumberPicker(picker, title, field)
    }

    private fun buildNumberPickerDialog(field: Int): MaterialNumberPicker {
        val numberPicker = MaterialNumberPicker.Builder(this)
                .minValue(1)
                .maxValue(SportTypesUtil.types.size)
                .defaultValue(1)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20f)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build()

        if (field == SPORT_TYPE) {
            numberPicker.setFormatter {
                value -> SportType.fromString(SportTypesUtil.types[value - 1].second).name
            }
        }

        return numberPicker
    }

    private fun showNumberPicker(numberPicker: MaterialNumberPicker, title: String, field: Int) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setView(numberPicker)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .setPositiveButton(getString(android.R.string.ok), { _, _ -> parseNumberPickerResult(numberPicker, field) })
                .show()
    }

    private fun parseNumberPickerResult(numberPicker: MaterialNumberPicker, field: Int) {
        if (field == SPORT_TYPE) {
            sportTypeId = SportType.fromString(SportTypesUtil.types[numberPicker.value - 1].second).id
            setText(field, SportType.fromString(SportTypesUtil.types[numberPicker.value - 1].second).name!!)
        } else {
            setText(field, numberPicker.value.toString())
        }
    }

    private fun setText(field: Int, value: String) {
        when (field) {
            USER_LIMIT -> userLimitValue.text = value
            SPORT_TYPE -> typeText.text = value
            TEAM_LIMIT -> teamLimitValue.text = value
        }
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
                this@CreateEventActivity,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setVersion(DatePickerDialog.Version.VERSION_2)
        datePicker.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        val timePicker = TimePickerDialog.newInstance(
                this@CreateEventActivity,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        )
        timePicker.show(fragmentManager, "Timepickerdialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateStart.text = DateUtils.getDateStringRepresentationWithoutTime(getDate(year, monthOfYear, dayOfMonth))
    }

    private fun getDate(year: Int, monthOfYear: Int, dayOfMonth: Int): Date {
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.YEAR, year)
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar.time
    }

    private fun getTime(hourOfDay: Int, minute: Int): Date {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar.time
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        if (isStartTimePicked) {
            startDate = getTime(hourOfDay, minute)
            timeStart.text = DateUtils.getTimeStringRepresentation(startDate)
        } else {
            endDate = getTime(hourOfDay, minute)
            endTime.text = DateUtils.getTimeStringRepresentation(endDate)
        }
    }

    companion object {
        var latLng: LatLng? = null
        var selectedLocation: String? = null
    }
}

