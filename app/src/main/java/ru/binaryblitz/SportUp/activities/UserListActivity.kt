package ru.binaryblitz.SportUp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity

class UserListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)

    }

}
