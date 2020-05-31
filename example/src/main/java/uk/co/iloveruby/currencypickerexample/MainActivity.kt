package uk.co.iloveruby.currencypickerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    private lateinit var currencyFragment: CurrencyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        currencyFragment = CurrencyFragment().apply {
            arguments = intent.extras
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, currencyFragment)
            .commit()
    }
}