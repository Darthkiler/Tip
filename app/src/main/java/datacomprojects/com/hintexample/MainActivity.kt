package datacomprojects.com.hintexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import darthkilersprojects.com.log.L
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

        hintsList.tipArrayList.add(Tip("qwe", hipsview1, view, hintsList))
        hintsList.tipArrayList.add(Tip("asd", hipsview2, view, hintsList))
        hintsList.tipArrayList.add(Tip("zxc", hipsview3, view, hintsList))

        hintsList.addAll()

        TipsSharedPreferencesUtils.putAllKeys(this,L.Utils.asList("qwe","asd","zxc"))

        hintsList.setHintShowedCallback(object : TipShowCallback() {
            override fun onDismiss(id: String?) {
                super.onDismiss(id)
                hintsList.showNext(this@MainActivity)
                L.show(hintsList.allWasShowed())
            }
        })

        hipsview1.setCloseImage(resources.getDrawable(R.drawable.ic_launcher_background))

        TipsSharedPreferencesUtils.removeFile(this)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ hintsList.showNext(this) }, 500)
        L.show(TipsSharedPreferencesUtils.isAllKeysTrue(this,L.Utils.asList("qwe","asd","zxc")))
    }

    override fun needToDismiss() {
        hintsList.dismissCurrent()
    }
}
