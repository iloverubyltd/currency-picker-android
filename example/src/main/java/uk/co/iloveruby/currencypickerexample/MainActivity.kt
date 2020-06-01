package uk.co.iloveruby.currencypickerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import uk.co.iloveruby.currencypickerexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var currencyFragment: CurrencyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
//        currencyFragment = CurrencyFragment().apply {
//            arguments = intent.extras
//        }
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container_view, currencyFragment)
//            .commit()
    }
}
