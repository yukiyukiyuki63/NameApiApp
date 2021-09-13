package jp.techacademy.yuuki.ishii.nameapiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class FavoriteAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<FavoriteShop>()

    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null // Favoriteから削除するときのメソッド(Adapter -> Fragment へ通知する)
    //    var onClickItem: ((String) -> Unit)? = null // Itemを押したときのメソッド
    var onClickItem: ((FavoriteShop) -> Unit)? = null // 課題:クーポン詳細ページでもお気に入りの追加削除

    fun refresh(list: List<FavoriteShop>) {
        items.apply {
            clear() // items を 空にする
            addAll(list) // itemsにlistを全て追加する
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size // お気に入りが1件もない時に、「お気に入りはありません」を出すため
    }

    override fun getItemViewType(position: Int): Int { // onCreateViewHolderの第二引数はここで決める。条件によってViewTypeを返す様にすると、一つのRecyclerViewで様々なViewがある物が作れる
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_EMPTY -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite_empty, parent, false))
            else -> FavoriteItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavoriteItemViewHolder)
            updateFavoriteItemViewHolder(holder, position)
    }

    private fun updateFavoriteItemViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            rootView.apply {
                setBackgroundColor(ContextCompat.getColor(context, if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray)) // 偶数番目と機数番目で背景色を変更させる
                setOnClickListener {
//                    onClickItem?.invoke(data.url)
                    onClickItem?.invoke(data) // 課題:クーポン詳細ページでもお気に入りの追加削除
                }
            }
            nameTextView.text = data.name
            addressTextView.text = data.address
            Picasso.get().load(data.imageUrl).into(imageView) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
            favoriteImageView.setOnClickListener {
                onClickDeleteFavorite?.invoke(data)
                notifyItemChanged(position)
            }
        }
    }

    class FavoriteItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val rootView : ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    class EmptyViewHolder(view: View): RecyclerView.ViewHolder(view)

    companion object {
        private const val VIEW_TYPE_ITEM = 0 // Viewの種類を表現する定数、こちらはお気に入りのお店
        private const val VIEW_TYPE_EMPTY = 1 // Viewの種類を表現する定数、こちらはお気に入りが１件もないとき
    }
}