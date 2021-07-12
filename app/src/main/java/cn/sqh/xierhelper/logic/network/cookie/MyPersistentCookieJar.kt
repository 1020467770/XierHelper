package cn.sqh.xierhelper.logic.network.cookie

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * 继承自某github创作的PersistentCookieJar
 * 但原来的Jar存储Cookie的方式是和浏览器同策略的，
 * 不会对服务器规定的Cookie生命期限为临时的Cookie进行保存，所以在自动登录的情况下不能验证同一个会话，
 * 因此创建了该类，会保存所有的Cookie并且加密然后持久化
 */
class MyPersistentCookieJar(val cache: CookieCache, val persistor: CookiePersistor) :
    PersistentCookieJar(cache, persistor) {

    init {
        this.cache.addAll(persistor.loadAll())
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cache.addAll(cookies)
        persistor.saveAll(cookies)
    }
}