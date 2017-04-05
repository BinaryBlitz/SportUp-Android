package ru.binaryblitz.SportUp.activities

import android.graphics.Color
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_event.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity
import ru.binaryblitz.SportUp.utils.AndroidUtilities

class EditEventActivity : BaseActivity() {
    val EXTRA_COLOR = "color"
    val DEFAULT_COLOR = Color.parseColor("#212121")

    var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        initToolbar()
    }

    private fun initToolbar() {
        color = intent.getIntExtra(EXTRA_COLOR, DEFAULT_COLOR)
        appBarView.setBackgroundColor(color)
        headerView.setBackgroundColor(color)
        AndroidUtilities.colorAndroidBar(this, color)
    }
}
