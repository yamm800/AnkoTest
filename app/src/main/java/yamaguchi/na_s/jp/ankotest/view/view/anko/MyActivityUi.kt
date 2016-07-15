package yamaguchi.na_s.jp.ankotest.view.view.anko

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import org.jetbrains.anko.*
import yamaguchi.na_s.jp.ankotest.model.user.User
import yamaguchi.na_s.jp.ankotest.model.user.UserList
import yamaguchi.na_s.jp.ankotest.presenter.ViewBinder
import yamaguchi.na_s.jp.ankotest.view.activity.MainActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Activity, V : View> UiComponent<T>.bindView(id: Int): ReadOnlyProperty<AnkoComponent<T>, V>
        = required(id, viewFinder)

private val <T : Activity> UiComponent<T>.viewFinder: AnkoComponent<T>.(Int) -> View?
    get() = { uiRoot.find(it) }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?)
        = Lazy { t: T, desc -> t.finder(id) as V? ?: viewNotFound(id, desc) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
        throw IllegalStateException("View ID $id for '${desc.name}' not found.")

private class Lazy<T, V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }
        @Suppress("UNCHECKED_CAST")
        return value as V
    }
}

open abstract class UiComponent<T : Activity> : AnkoComponent<T> {
    abstract var uiRoot: View
}

/**
 * Created by yamaguchi on 16/07/11.
 */
class MyActivityUI() : UiComponent<MainActivity>() {

    override lateinit var uiRoot: View

    var userList: UserList? by ViewBinder {
        textView.text = userList?.date
        listViewAdapter.userList = userList
        listViewAdapter.notifyDataSetChanged()
    }

    val frameLayoutId = View.generateViewId()

    val containerId = View.generateViewId()
    val container: LinearLayout by bindView(containerId)

    val textviewId = View.generateViewId()
    val textView: TextView by bindView(textviewId)

    val listViewId = View.generateViewId()
    val listview: ListView by bindView(listViewId)

    lateinit var listViewAdapter: MyListViewAdapter

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        verticalLayout {
            textView {
                id = textviewId
                text = userList?.date
                textChangedListener {
                    afterTextChanged {
                        userList = userList?.copy(it.toString())
                    }
                }
            }
            val name = editText()
            button("EDIT") {
                // Viewを更新
                onClick { textView.text = name.text }
            }

            frameLayout {
                id = frameLayoutId
                // Fragmentを直書きするとプレビューしてくれなくなる
//                owner.supportFragmentManager.beginTransaction().replace(id, MainFragment()).commit()
            }

            verticalLayout {
                id = containerId
            }

            listView {
                id = listViewId
                listViewAdapter = MyListViewAdapter(ctx, userList)
                adapter = listViewAdapter
            }

            uiRoot = this
        }
    }
}

class MyListViewAdapter(var context: Context, var userList: UserList? = null) : BaseAdapter() {

    override fun getItem(position: Int): User? {
        return userList?.list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return userList?.list?.get(position)?.id ?: 0L
    }

    override fun getCount(): Int {
        return userList?.list?.size ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var newConvertView: View?

        if (convertView == null) {
            var ui = MyListItemUI(getItem(position), position)
            newConvertView = ui.createView(context.UI { })
            newConvertView.tag = ui
        } else {
            newConvertView = convertView
            var ui = newConvertView.tag as MyListItemUI
            ui.label?.text = "List item: " + position
            newConvertView.tag = ui
        }

        return newConvertView
    }
}

class MyListItemUI(var user: User?, var position: Int) : AnkoComponent<Context> {
    lateinit var label: TextView

    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        verticalLayout {
            label = textView {
                text = "List item: " + position
            }
        }
    }
}