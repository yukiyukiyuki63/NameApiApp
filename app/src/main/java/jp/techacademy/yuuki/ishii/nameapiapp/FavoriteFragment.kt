package jp.techacademy.yuuki.ishii.nameapiapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment: Fragment() {

    private val favoriteAdapter by lazy { FavoriteAdapter(requireContext()) }

    private var fragmentCallback: FragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //初期化処理を実施
        favoriteAdapter.apply {
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }

        recyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }

        updateData()
    }


    fun updateData(){
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }

    //画面切り替え時にも情報更新
    override fun onResume(){
        super.onResume()
        updateData()
    }

    //onResumeを追加
    //onResumeの中でupdateDataを呼ぶ
    /*

     */

}