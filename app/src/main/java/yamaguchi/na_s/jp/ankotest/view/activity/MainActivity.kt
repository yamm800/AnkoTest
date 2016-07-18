package yamaguchi.na_s.jp.ankotest.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.jetbrains.anko.*
import yamaguchi.na_s.jp.ankotest.model.user.User
import yamaguchi.na_s.jp.ankotest.model.user.UserList
import yamaguchi.na_s.jp.ankotest.view.fragment.MainFragment
import yamaguchi.na_s.jp.ankotest.view.view.anko.MyActivityUI
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ui = MyActivityUI()
        ui.setContentView(this)
        
        ui.userList = UserList("20160711", arrayListOf(User(0, "name"), User(0, "name"), User(0, "name")))
        while (ui.listViewAdapter.count < 50) {
            ui.userList?.list?.add(User(0, "name"))
        }

        ui.container?.addView(UI {
            verticalLayout {
                linearLayout {
                    textView("Add View").lparams(width = 0, weight = 1f)
                }
            }
        }.view, 0)

        supportFragmentManager.beginTransaction().replace(ui.frameLayoutId, MainFragment()).commit()
    }
}