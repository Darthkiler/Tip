package datacomprojects.com.hintexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import datacomprojects.com.hint.Hint
import datacomprojects.com.hint.HintsList
import datacomprojects.com.hint.TipsSharedPreferencesUtils
import datacomprojects.com.hint.callbacks.HintNeedToDismissHintInterface
import datacomprojects.com.hint.callbacks.HintShowCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HintNeedToDismissHintInterface {


    lateinit var hintsList : HintsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hintsList= HintsList(this)

        hintsList.hintArrayList.add(Hint("qwe", hipsview1, view, hintsList))
        hintsList.hintArrayList.add(Hint("asd", hipsview2, view, hintsList))
        hintsList.hintArrayList.add(Hint("zxc", hipsview3, view, hintsList))

        hintsList.addAll()

        hintsList.setHintShowedCallback(object : HintShowCallback() {
            override fun onDismiss(id: String?) {
                super.onDismiss(id)
                hintsList.showNext(this@MainActivity)
            }
        })

        TipsSharedPreferencesUtils.removeFile(this)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ hintsList.showNext(this) }, 500)

    }

    fun click(view: View) {
        //hintsList.dismissCurrent()
    }

    override fun needToDismiss() {
        hintsList.dismissCurrent()
    }
}
