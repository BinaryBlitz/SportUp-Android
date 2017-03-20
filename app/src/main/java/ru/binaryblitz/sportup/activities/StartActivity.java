package ru.binaryblitz.sportup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ru.binaryblitz.sportup.R;
import ru.binaryblitz.sportup.base.BaseActivity;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, SelectCityActivity.class));
                finish();
            }
        });
    }

}

