package jp.techacademy.yuuki.ishii.nameapiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.recycler_favorite.*

class WebViewActivity: AppCompatActivity() {

    //Favoriteshopのインスタンスを作成
    private var favoriteShop: FavoriteShop = FavoriteShop()


    private var isFavorite = false
        set(value) {
        if (field == value)
            return
        field = value
/*            favoriteShop.id = intent.getStringExtra(KEY_FAVORITESHOP_ID).toString()
            favoriteShop.name = intent.getStringExtra(KEY_FAVORITESHOP_NAME).toString()
            favoriteShop.imageUrl = intent.getStringExtra(KEY_FAVORITESHOP_IMAGEURL).toString()
            favoriteShop.url = intent.getStringExtra(KEY_FAVORITESHOP_URL).toString()
            favoriteShop.address = intent.getStringExtra(KEY_FAVORITESHOP_ADDRESS).toString()
 */       updateFavorite(value)
        invalidateOptionsMenu()
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView.loadUrl(intent.getStringExtra(KEY_FAVORITESHOP_URL).toString())
        //20210911　favoriteshopが型が違うため直接投げれないので、errorが発生
//        favoriteShop = intent.getSerializableExtra(KEY_FAVORITESHOP) as? FavoriteShop ?: return { finish() }()
        favoriteShop.id = intent.getStringExtra(KEY_FAVORITESHOP_ID).toString()
        favoriteShop.name = intent.getStringExtra(KEY_FAVORITESHOP_NAME).toString()
        favoriteShop.imageUrl = intent.getStringExtra(KEY_FAVORITESHOP_IMAGEURL).toString()
        favoriteShop.url = intent.getStringExtra(KEY_FAVORITESHOP_URL).toString()
        favoriteShop.address = intent.getStringExtra(KEY_FAVORITESHOP_ADDRESS).toString()
//        webView.loadUrl(KEY_FAVORITESHOP_URL)
        //findby(id)

        isFavorite = FavoriteShop.findBy(favoriteShop.id) != null
    }

    //メニューバーに星を表示させる
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.meue_web_view_activity, menu)
        menuInflater.inflate(R.menu.menu_web_main_activity, menu)
        menu?.findItem(R.id.actionFavorite)?.setIcon(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when(item.itemId) {
            R.id.actionFavorite -> {
                isFavorite = !isFavorite
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    //deleteやinsertはボタン（★）のアクションで操作させる
    // FavoriteShop.delete(Favoriteshop.id)
    //FavoriteShop.insert(Favoriteshop) →　Favoriteをまるっと投げる
    //idを見てdeleteするような処理
    private fun updateFavorite(willFavorite: Boolean) {
        if (willFavorite)
            //20210911ここでエラーが出ているみたい
            FavoriteShop.insert(favoriteShop)
        else
            FavoriteShop.delete(favoriteShop.id)
    }

    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_FAVORITESHOP = "key_favoriteshop"
        private const val KEY_FAVORITESHOP_NAME = ""
        private const val KEY_FAVORITESHOP_ID = "key_favoriteshop_id"
        private const val KEY_FAVORITESHOP_IMAGEURL = "key_favoriteshop_imageUrl"
        private const val KEY_FAVORITESHOP_URL = "key_favoriteshop_Url"
        private const val KEY_FAVORITESHOP_ADDRESS = "key_favoriteshop_Addressl"

        fun start(activity: Activity, favoriteShop: FavoriteShop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java)
                    //putExtraでは型が違うため投げれない
//                    .putExtra(KEY_FAVORITESHOP, favoriteShop)
                    .putExtra(KEY_FAVORITESHOP_NAME, favoriteShop.name)
                    .putExtra(KEY_FAVORITESHOP_ID, favoriteShop.id)
                    .putExtra(KEY_FAVORITESHOP_IMAGEURL, favoriteShop.imageUrl)
                    .putExtra(KEY_FAVORITESHOP_URL, favoriteShop.url)
                    .putExtra(KEY_FAVORITESHOP_ADDRESS, favoriteShop.address)
            )


        }

        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(

                Intent(activity, WebViewActivity::class.java)
                    .putExtra(KEY_URL, shop.couponUrls.sp)
            )
        }
    }
}