package uk.co.iloveruby.currencypickerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.fragment.app.replace
import androidx.preference.PreferenceManager
import uk.co.iloveruby.currencypickerexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.fragment_container, CurrencyFragment.newInstance(intent.extras))
            }
        }
    }
}
