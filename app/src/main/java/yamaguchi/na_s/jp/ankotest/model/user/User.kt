package yamaguchi.na_s.jp.ankotest.model.user

import java.util.*

/**
 * Created by yamaguchi on 16/07/08.
 */
data class UserList(var date: String, var list: ArrayList<User>)

data class User(var id: Long? = 0, var name: String? = "")