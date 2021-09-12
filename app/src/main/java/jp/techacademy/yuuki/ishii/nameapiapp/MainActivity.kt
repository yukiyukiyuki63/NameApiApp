package jp.techacademy.yuuki.ishii.nameapiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentCallback {

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    private val items = mutableListOf<Shop>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //viewpager2の初期化
        viewPager2.apply{
            adapter = viewPagerAdapter

            //スワイプの向き　横向きの指定
            orientation = ViewPager2.ORIENTATION_HORIZONTAL

            //viewpager2で保持する画面数
            offscreenPageLimit = viewPagerAdapter.itemCount

        }

        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            // TabLayoutの初期化
            // TabLayoutとViewPager2を紐づける
            // TabLayoutのTextを指定する
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    override fun onClickItem(favoriteShop: FavoriteShop) {
        WebViewActivity.start(this, favoriteShop)
    }

    override fun onClickItem(shop: Shop) {
            onClickItem(FavoriteShop().apply {
                id = shop.id
                name = shop.name
                imageUrl = shop.logoImage
//            address = shop.address
                url = if(shop.couponUrls.sp.isNotEmpty()){
                    shop.couponUrls.sp
                } else shop.couponUrls.pc
            })
    }



    override fun onAddFavorite(shop: Shop){
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
//            address = shop.address
            url = if(shop.couponUrls.sp.isNotEmpty()){
                shop.couponUrls.sp
            } else shop.couponUrls.pc
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    override fun onDeleteFavorite(id: String) { // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    companion object {
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }
}