package ru.binaryblitz.SportUp.activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_create_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.models.SportType
import ru.binaryblitz.SportUp.utils.DateUtils
import ru.binaryblitz.SportUp.utils.SportTypesUtil
import java.util.*

class CreateEventActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    var isStartTimePicked = true

    val USER_LIMIT = 1
    val SPORT_TYPE = 2
    val TEAM_LIMIT = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        SportTypesUtil.load(this)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        dateStart.setOnClickListener { showDatePicker() }

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
        if (view == null) {
            return
        }

        dateStart.text = DateUtils.getDateStringRepresentationWithoutTime(getDate(year, monthOfYear, dayOfMonth))
    }

    private fun getDate(year: Int, monthOfYear: Int, dayOfMonth: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.YEAR, year)
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar.time
    }

    private fun getTime(hourOfDay: Int, minute: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        return calendar.time
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        if (isStartTimePicked) {
            timeStart.text = DateUtils.getTimeStringRepresentation(getTime(hourOfDay, minute))
        } else {
            endTime.text = DateUtils.getTimeStringRepresentation(getTime(hourOfDay, minute))
        }
    }
}

