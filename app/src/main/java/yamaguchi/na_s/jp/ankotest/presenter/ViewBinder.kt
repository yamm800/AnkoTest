package yamaguchi.na_s.jp.ankotest.presenter

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by yamaguchi on 16/07/11.
 */
class ViewBinder<M>(val function: (M) -> Unit) : ReadWriteProperty<Any, M> {
    private var mValue: M? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): M {
        return mValue as M
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: M) {
        if (mValue == value) {
            return
        }
        mValue = value
        function(value)
    }
}