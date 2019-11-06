package datacomprojects.com.hintexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import datacomprojects.com.hint.Tip
import datacomprojects.com.hint.TipsList
import datacomprojects.com.hint.TipsSharedPreferencesUtils
import datacomprojects.com.hint.callbacks.TipNeedToDismissTipInterface
import datacomprojects.com.hint.callbacks.TipShowCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TipNeedToDismissTipInterface {

    lateinit var hintsList : TipsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hintsList= TipsList(this)

        hintsList.tipArrayList.add(Tip("qwe", hipsview1, view, resources.getDrawable(R.drawable.ic_launcher_background, applicationContext.theme)))
        hintsList.tipArrayList.add(Tip("asd", hipsview2, view, null))
        hintsList.tipArrayList.add(Tip("zxc", hipsview3, view))

        hintsList.addAll()


        hintsList.setHintShowedCallback(object : TipShowCallback() {
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

    override fun needToDismiss() {
        hintsList.dismissCurrent()
    }
}
