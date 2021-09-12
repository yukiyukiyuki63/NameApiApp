package jp.techacademy.yuuki.ishii.nameapiapp

interface FragmentCallback {
    fun onClickItem(shop: Shop)
    //面談にてアドバイス
    fun onClickItem(favoriteShop: FavoriteShop)
//    fun onClickItem(url: String)
    //お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)
    //お気に入り削除時の処理
    fun onDeleteFavorite(id: String)

}
