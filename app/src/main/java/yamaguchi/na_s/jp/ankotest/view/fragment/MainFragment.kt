package yamaguchi.na_s.jp.ankotest.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

/**
 * Created by yamaguchi on 16/07/06.
 */
class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return UI {
            verticalLayout {
                linearLayout {
                    textView("This is fragment.")
                }
            }
        }.view
    }
}