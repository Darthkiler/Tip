package datacomprojects.com.hintexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import datacomprojects.com.hint.Tip
import datacomprojects.com.hint.TipsList
import datacomprojects.com.hint.TipsSharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var hintsList : TipsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hintsList= TipsList(this)

        hintsList.addTipToArray(Tip("qwe", hipsview1, view, resources.getDrawable(R.drawable.ic_launcher_background, applicationContext.theme)))
        hintsList.addTipToArray(Tip("asd", hipsview2, view, null))
        hintsList.addTipToArray(Tip("zxc", hipsview3, view, ContextCompat.getDrawable(this, R.drawable.ic_cancel_ic_tip)))

        hintsList.addAll()

        TipsSharedPreferencesUtils.removeFile(this)
    }

    fun post(view: View) {
        Handler().postDelayed({ hintsList.showNext() }, 500)
    }

    fun stop(view: View) {
        hintsList.skip()
    }

    fun dismissAll(view: View) {
        hintsList.dismissAll()
    }

    fun wasAllShowed(view: View) {
        Toast.makeText(this, hintsList.allWasShowed().toString(), Toast.LENGTH_LONG).show()
    }
}
