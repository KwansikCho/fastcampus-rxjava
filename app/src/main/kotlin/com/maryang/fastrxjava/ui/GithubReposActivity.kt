package com.maryang.fastrxjava.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.base.BaseApplication
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.User
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_github_repos.*


class GithubReposActivity : AppCompatActivity() {

    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { load() }

        load(true)
    }

    // 백버튼을 누르면 Activity는 종료됨
    // Activity에 있는 Observable이 구독하는 Observer는 Activity의 Context를 참조함
    // Dispose되지 않는 Observer에 Context가 잡혀있어 사라지지 않음
    private fun load(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()
        viewModel.getGithubRepos()
            .subscribe(object : DisposableSingleObserver<List<GithubRepo>>() {
                override fun onError(e: Throwable) {
                    hideLoading()
                }

                override fun onSuccess(t: List<GithubRepo>) {
                    hideLoading()
                    adapter.items = t
                    adapter.notifyDataSetChanged()
                }
            })

        viewModel.getGithubRepos().toMaybe()
            .doOnSuccess {
                //getGithubRepos가 종료되면 로그가 불림
                Log.d(BaseApplication.TAG, "getGithubRepos()")
            }
                //getUser 부름
            .flatMap { viewModel.getUser() }
            .doOnSuccess{
                //getUser가 종료되면 로그가 불림
                Log.d(BaseApplication.TAG, "getUser()")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

            )

        Single.zip<List<GithubRepo>>()
    }


    private fun load2(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()
        viewModel.getUser()
            .subscribe(object : DisposableMaybeObserver<User>() {
                override fun onComplete() {
                    //널이면 불리는 곳
                }

                override fun onError(e: Throwable) {
                    //에러나면 불리는 곳
                }

                override fun onSuccess(t: User) {
                    //널이 아니면 불리는 곳
                }
            })
    }

    private fun load3(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()
        viewModel.updateUSer()
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    //완료되면 불리는 곳
                }

                override fun onError(e: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }
}
