package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_edit_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.utils.*

class EditEventActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val DEFAULT_COLOR = Color.parseColor("#212121")
    lateinit var event: JsonObject
    var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        event = EventActivity.eventJson
        initToolbar()
        loadEventInformation()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        backBtn.setOnClickListener { finish() }

        rightBtn.setOnClickListener {

        }

        isPublicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showPasswordView()
            } else {
                hidePasswordView()
            }
        }
    }

    private fun initToolbar() {
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        headerView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }

    private fun loadEventInformation() {
        nameEdit.setText(event.get("name").asString)
        descriptionEdit.setText(AndroidUtilities.getStringFieldFromJson(event.get("description")))
        locationText.text = AndroidUtilities.getStringFieldFromJson(event.get("address"))
        priceText.text = AndroidUtilities.getStringFieldFromJson(event.get("price")) + getString(R.string.ruble_sign)

        parseMembersInfo(event)
        parseTime(event)
        parseSportType()
    }

    private fun parseMembersInfo(obj: JsonObject) {
        userLimitValue.text = obj.get("user_limit").asString
        teamLimitValue.text = obj.get("team_limit").asString
        membersInformation.text = getString(R.string.joined_code) + obj.get("user_count").asString + getString(R.string.from_code) + obj.get("user_limit").asString
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

        Image.loadPhoto(SportTypesUtil.findIcon(this, EventActivity.sportTypeId!!), sportTypeIcon)
        sportTypeIcon.setColorFilter(SportTypesUtil.findColor(this, EventActivity.sportTypeId!!))
        sportTypeIndicator.setColorFilter(SportTypesUtil.findColor(this, EventActivity.sportTypeId!!))
        typeText.text = SportTypesUtil.findName(this, EventActivity.sportTypeId!!)
        typeText.setTextColor(SportTypesUtil.findColor(this, EventActivity.sportTypeId!!))
    }

    private fun parseTime(obj: JsonObject) {
        dateStart.text = DateUtils.getDateStringRepresentationWithoutTime(
                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at"))))
        timeStart.text = DateUtils.getTimeStringRepresentation(
                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("starts_at"))))
        endTime.text = DateUtils.getTimeStringRepresentation(
                DateUtils.parse(AndroidUtilities.getStringFieldFromJson(obj.get("ends_at"))))

    }
}
