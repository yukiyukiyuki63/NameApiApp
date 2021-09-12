package jp.techacademy.yuuki.ishii.nameapiapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiFragment: Fragment() {

    private val apiAdapter by lazy { ApiAdapter(requireContext()) }
    private val handler = Handler(Looper.getMainLooper())

    private var fragmentCallback : FragmentCallback? = null // Fragment -> Activity にFavoriteの変更を通知する

    private var page = 0
    private var isLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_api, container, false) // fragment_api.xmlが反映されたViewを作成して、returnします
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // ApiAdapterのお気に入り追加、削除用のメソッドの追加を行う
        apiAdapter.apply {
            onClickAddFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onAddFavorite(it)
            }
            onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            onCLickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示

            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy == 0){
                        return
                    }
                    //Recycleviewの現在の表示数
                    val totalCount = apiAdapter.itemCount
                    //現在見えている最後のViewHolderのposition
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    // totalCountとlastVisibleItemから全体のアイテム数のうちどこまでが見えているかがわかる
                    // (例:totalCountが20、lastVisibleItemが15の時は、
                    // 現在のスクロール位置から下に5件見えていないアイテムがある)
                    // 一番下にスクロールした時に次の20件を表示する等の実装が可能になる。
                    // ユーザビリティを考えると、一番下にスクロールしてから追加した場合、一度スクロールが止まるので、
                    // ユーザーは気付きにくい
                    // ここでは、一番下から5番目を表示した時に追加読み込みする様に実装する
                    // 読み込み中でない、かつ、現在のスクロール位置から下に5件見えていないアイテムがある
                    if (!isLoading && lastVisibleItem >= totalCount - 6){
                        updateData(true)
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateView() { // お気に入りが削除されたときの処理（Activityからコールされる）
        recyclerView.adapter?.notifyDataSetChanged() // RecyclerViewのAdapterに対して再描画のリクエストをする
    }

    //
    override fun onResume() {
        super.onResume()
        updateView()
    }
    private fun updateData(isAdd: Boolean = false) {
        if(isLoading){
            return
        } else {
            isLoading = true
        }
        if(isAdd){
            page ++
        } else {
            page = 0
        }
        val start = page * COUNT + 1
        val url = StringBuilder()
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key)) // Apiを使うためのApiKey
            .append("&start=").append(1) // 何件目からのデータを取得するか
            .append("&count=").append(COUNT) // 1回で20件取得する
            .append("&keyword=").append(getString(R.string.api_keyword)) // お店の検索ワード。ここでは例として「ランチ」を検索
            .append("&format=json") // ここで利用しているAPIは戻りの形をxmlかjsonが選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
            .toString()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) { // Error時の処理
                e.printStackTrace()
                handler.post {
                    updateRecyclerView(listOf(), isAdd)
                }
                isLoading = false
            }
            override fun onResponse(call: Call, response: Response) { // 成功時の処理
                var list = listOf<Shop>()
                response.body?.string()?.also {
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post {
                    updateRecyclerView(list, isAdd)
                }
                isLoading = false
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>, isAdd: Boolean) {
        if(isAdd) {
            apiAdapter.add(list)
        } else {
            apiAdapter.refresh(list)
        }
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }

    companion object {
        private const val COUNT = 20 // 1回のAPIで取得する件数
    }
}