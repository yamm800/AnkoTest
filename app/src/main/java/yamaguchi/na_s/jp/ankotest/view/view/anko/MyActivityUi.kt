package yamaguchi.na_s.jp.ankotest.view.view.anko

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.bindView
import org.jetbrains.anko.*
import yamaguchi.na_s.jp.ankotest.model.user.User
import yamaguchi.na_s.jp.ankotest.model.user.UserList
import yamaguchi.na_s.jp.ankotest.presenter.ViewBinder
import yamaguchi.na_s.jp.ankotest.view.activity.MainActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified V : View> UiComponent<*>.bind(id: Int): kotlin.Lazy<V> =
        lazy {
            root.find<V>(id)
        }

fun <T : Context, V : View> UiComponent<T>.bindView(id: Int): ReadOnlyProperty<AnkoComponent<T>, V>
        = required(id, viewFinder)

private val <T : Context> UiComponent<T>.viewFinder: AnkoComponent<T>.(Int) -> View?
    get() = { root.find(it) }

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

// プロパティの名前からViewを取得する
//private fun <T : Context> UiComponent<T>.findViewByName(name: String): View? =
//        try {
//            var c : KClass<UiComponent<T>> = UiComponent<T>::class
//            c.members.filter { it as KProperty }
//            val resIds = Class.forName(getPackageName() + ".R\$id")
//            val resId = resIds.getField(name).get(resIds) as Int
//            findViewById(resId)
//        } catch(e: Exception) {
//            null
//        }

abstract class UiComponent<T : Context> : AnkoComponent<T> {
    lateinit var root: View
}

/**
 * Created by yamaguchi on 16/07/11.
 */
class MyActivityUI() : UiComponent<MainActivity>() {

    var userList: UserList? by ViewBinder {
        textView.text = userList?.date
        listViewAdapter.userList = userList
        listViewAdapter.notifyDataSetChanged()
    }

    val frameLayoutId = View.generateViewId()

    val containerId = View.generateViewId()
    //    val container: LinearLayout by bindView(containerId)
    val container by lazy {
        root.find<LinearLayout>(containerId)
    }

    val textviewId = View.generateViewId()
    val textView: TextView by bind(textviewId)

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
                listViewAdapter = MyListViewAdapter(owner)
                adapter = listViewAdapter
            }

            root = this
        }
    }
}

class MyListViewAdapter(var context: Context) : BaseAdapter() {

    var userList: UserList? = null

    override fun getItem(position: Int): User? {
        return userList?.list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return userList?.list?.get(position)?.id ?: 0L
    }

    override fun getCount(): Int {
        return userList?.list?.size ?: 0
    }

    override fun getView(
            position: Int, convertView: View?, parent: ViewGroup?): View =
            ((convertView as? MyListItemUI) ?: MyListItemUI(context).apply {
                // convertViewが nullの時には Viewを作成
                createView(context.UI {})
            }).apply {
                // Viewを更新
                update(position)
            }
}

//class MyListItemUI(context: Context) : FrameLayout(context) {
//
//    private var label: TextView? = null
//
//    init {
//        context.UI {
//            verticalLayout {
//                label = textView {
//
//                }
//            }.apply { this@MyListItemUI.addView(this) }
//        }
//    }
//
//    fun update(position: Int) {
//        label?.text = "List item: " + position
//    }
//}

class MyListItemUI(context: Context) : FrameLayout(context), AnkoComponent<Context> {

    val labelId = View.generateViewId()
    val label: TextView by bindView(labelId)

    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        verticalLayout {
            textView {
                id = labelId
            }
        }.apply { this@MyListItemUI.addView(this) }
    }

    fun update(position: Int) {
        label?.text = "List item: " + position
    }
}

//class MyListItemUI(var user: User?, var position: Int) : UiComponent<Context>() {
//
//    val labelId = View.generateViewId()
//    val label: TextView by bindView(labelId)
//
//    override fun createView(ui: AnkoContext<Context>) = with(ui) {
//        verticalLayout {
//            textView {
//                id = labelId
//                text = "List item: " + position
//            }
//
//            root = this
//        }
//    }
//}