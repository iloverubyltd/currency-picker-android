package com.mynameismidori.currencypickerexample;

import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private CurrencyFragment currencyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        currencyFragment = new CurrencyFragment();
        currencyFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, currencyFragment).commit();
    }

}