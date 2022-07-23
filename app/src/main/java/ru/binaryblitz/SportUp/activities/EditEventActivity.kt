package ru.binaryblitz.SportUp.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputType
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_edit_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.presenters.EditEventPresenter
import ru.binaryblitz.SportUp.server.DeviceInfoStore
import ru.binaryblitz.SportUp.server.EndpointsService
import ru.binaryblitz.SportUp.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditEventActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener  {
    val EXTRA_COLOR = "color"
    val EXTRA_EDIT = "edit"

    val DEFAULT_COLOR = Color.parseColor("#212121")
    lateinit var event: JsonObject
    var color = 0

    var isStartTimePicked = true

    val USER_LIMIT = 1
    val SPORT_TYPE = 2
    val TEAM_LIMIT = 3

    var startDate: Date? = null
    var endDate: Date? = null
    val calendar: Calendar = Calendar.getInstance()
    var sportTypeId = 0

    var errorString = ""
    var error = false

    lateinit var dialog: ProgressDialog

    lateinit var presenter: EditEventPresenter

    @Inject
    lateinit var api: EndpointsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)
        dependencies()!!.inject(this)

        SportTypesUtil.load(this)
        event = EventActivity.eventJson

        initToolbar()
        loadEventInformation()
        setOnClickListeners()
    }

    private fun showDeleteDialog() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.delete_event))
                .content(getString(R.string.are_you_sure))
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive { _, _ ->
                    deleteEvent()
                }
                .show()
    }

    private fun deleteEvent() {
        presenter.deleteEvent(event.get("id").asInt, DeviceInfoStore.getToken(this))
    }

    fun onEventDeleted() {
        val intent = Intent(this@EditEventActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setOnClickListeners() {
        backButton.setOnClickListener { finish() }

        rightButton.setOnClickListener {
            sendEvent()
        }

        isPublicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showPasswordView()
            } else {
                hidePasswordView()
            }
        }

        dateStart.setOnClickListener { showDatePicker() }

        priceButton.setOnClickListener { showPriceDialog() }

        passwordView.setOnClickListener { showPasswordDialog() }

        deleteEventButton.setOnClickListener { showDeleteDialog() }

        locationButton.setOnClickListener {
            val intent = Intent(this@EditEventActivity, MapActivity::class.java)
            intent.putExtra(EXTRA_EDIT, true)
            startActivity(intent)
        }

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

    private fun initFormat(): SimpleDateFormat {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")

        return format
    }

    private fun initToolbar() {
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        headerView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
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
        error = false
        errorString = ""

        checkCondition(nameEdit.text.toString().isEmpty(), R.string.event_name_error)
        checkCondition(CreateEventActivity.latLng == null, R.string.wrong_event_location)
        checkCondition(!DateUtils.isAfterToday(startDate), R.string.wrong_event_date)
        checkCondition(!DateUtils.isAfter(startDate, endDate), R.string.wrong_event_end_date)
        checkCondition(userLimitValue.text.toString() == "0", R.string.wrong_event_user_limit)
        checkCondition(teamLimitValue.text.toString() == "0", R.string.wrong_event_team_limit)
        checkCondition(sportTypeId == 0, R.string.wrong_event_sport_type)

        if (isPublicSwitch.isChecked) {
            checkCondition(passwordValue.text.toString() == getString(R.string.select_password), R.string.password_error)
        }
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
        event.addProperty("latitude", CreateEventActivity.latLng!!.latitude)
        event.addProperty("longitude", CreateEventActivity.latLng!!.longitude)
        event.addProperty("user_limit", userLimitValue.text.toString())
        event.addProperty("team_limit", teamLimitValue.text.toString())
        event.addProperty("description", descriptionEdit.text.toString())
        event.addProperty("price", priceText.text.toString().split(" ")[0].toInt())
        event.addProperty("sport_type_id", sportTypeId)
        event.addProperty("city_id", DeviceInfoStore.getCityObject(this)?.id)
        event.addProperty("public", !isPublicSwitch.isChecked)
        if (isPublicSwitch.isChecked) {
            event.addProperty("password", passwordValue.text.toString())
        }

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

        presenter = EditEventPresenter(api, this)
        presenter.editEvent(event.get("id").asInt, generateJson(), DeviceInfoStore.getToken(this))
    }

    fun onLoaded() {
        dialog.dismiss()
        finish()
    }

    private fun showErrorDialog() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.error))
                .content(errorString)
                .positiveText(getString(R.string.ok))
                .show()
    }

    private fun loadEventInformation() {
        nameEdit.setText(event.get("name").asString)
        descriptionEdit.setText(AndroidUtilities.getStringFieldFromJson(event.get("description")))
        locationText.text = AndroidUtilities.getStringFieldFromJson(event.get("address"))
        priceText.text = AndroidUtilities.getStringFieldFromJson(event.get("price")) + getString(R.string.ruble_sign)

        CreateEventActivity.latLng = LatLng(AndroidUtilities.getDoubleFieldFromJson(event.get("latitude")),
                AndroidUtilities.getDoubleFieldFromJson(event.get("longitude")))

        parseMembersInfo(event)
        parseTime(event)
        parseSportType()
    }

    private fun parseMembersInfo(obj: JsonObject) {
        userLimitValue.text = obj.get("user_limit").asString
        teamLimitValue.text = obj.get("team_limit").asString
        membersInformation.text = String.format(resources.getString(R.string.joined_code),
                obj.get("user_count").asString, obj.get("user_limit").asString)
    }

    private fun showPasswordView() {
        Animations.animateRevealShow(passwordView, this)
    }

    private fun hidePasswordView() {
        Animations.animateRevealHide(passwordView)
    }

    private fun parseSportType() {
        if (EventActivity.sportTypeId == null) {
            return
        }

        this.sportTypeId = EventActivity.sportTypeId!!
        Image.loadPhoto(SportTypesUtil.findIcon(this, sportTypeId), sportTypeIcon)
        sportTypeIcon.setColorFilter(SportTypesUtil.findColor(this, sportTypeId))
        sportTypeIndicator.setColorFilter(SportTypesUtil.findColor(this, sportTypeId))
        typeText.text = SportTypesUtil.findName(this, sportTypeId)
        typeText.setTextColor(SportTypesUtil.findColor(this, sportTypeId))
    }

    private fun showNumberPicker(title: String, field: Int) {
        val picker = buildNumberPickerDialog(field)
        showNumberPicker(picker, title, field)
    }

    private fun buildNumberPickerDialog(field: Int): MaterialNumberPicker {
        val numberPicker = MaterialNumberPicker.Builder(this)
                .minValue(1)
                .maxValue(1000)
                .defaultValue(1)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20f)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build()

        if (field == SPORT_TYPE) {
            numberPicker.maxValue = SportTypesUtil.types.size
            numberPicker.setFormatter { value -> SportTypesUtil.types[value - 1].second }
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
            sportTypeId = SportTypesUtil.types[numberPicker.value - 1].first
            chooseSportType(sportTypeId)
        } else {
            setText(field, numberPicker.value.toString())
        }
    }

    private fun setText(field: Int, value: String) {
        when (field) {
            USER_LIMIT -> userLimitValue.text = value
            TEAM_LIMIT -> teamLimitValue.text = value
        }
    }

    private fun chooseSportType(sportTypeId: Int) {
        Image.loadPhoto(SportTypesUtil.findIcon(this, sportTypeId), sportTypeIcon)
        val color = SportTypesUtil.findColor(this, sportTypeId)
        EventActivity.color = color
        sportTypeIcon.setColorFilter(color)
        sportTypeIndicator.setColorFilter(color)
        typeText.text = SportTypesUtil.findName(this, sportTypeId)
        typeText.setTextColor(color)

        appBarView.setBackgroundColor(color)
        headerView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }

    private fun showPasswordDialog() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.set_password))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(5, 5, ContextCompat.getColor(this, R.color.redColor))
                .input(getString(R.string.password), "") { _, input ->
                    passwordValue.text = input.toString()
                }.show()
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
                this@EditEventActivity,
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
                this@EditEventActivity,
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

    private fun parseTime(obj: JsonObject) {
        startDate = DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at")))
        dateStart.text = DateUtils.getDateStringRepresentationWithoutTime(startDate)
        timeStart.text = DateUtils.getTimeStringRepresentation(startDate)

        parseEndDate(obj)
        endTime.text = DateUtils.getTimeStringRepresentation(endDate)
    }

    private fun parseEndDate(obj: JsonObject) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        endDate = DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("ends_at")))

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        calendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE))

        endDate = calendar.time
    }
}
