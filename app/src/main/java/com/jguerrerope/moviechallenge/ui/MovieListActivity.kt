package com.jguerrerope.moviechallenge.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jguerrerope.moviechallenge.R
import com.jguerrerope.moviechallenge.data.NetworkState
import com.jguerrerope.moviechallenge.data.Status
import com.jguerrerope.moviechallenge.di.Injectable
import com.jguerrerope.moviechallenge.extension.observe
import com.jguerrerope.moviechallenge.extension.visibleOrGone
import com.jguerrerope.moviechallenge.ui.adapter.MoviePagedListAdapter
import com.jguerrerope.moviechallenge.ui.viewmodel.MoviePopularViewModel
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class MovieListActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MoviePopularViewModel
    private lateinit var adapter: MoviePagedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_fade_in, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        setUpViews()
        setUpViewModels()
    }

    private fun setUpViews() {
        adapter = MoviePagedListAdapter {
            startActivity<MovieDetailsActivity>()
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MovieListActivity)
            adapter = this@MovieListActivity.adapter
        }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MoviePopularViewModel::class.java)
        viewModel.moviesPopular.observe(this) {
            adapter.submitList(it)
        }
        viewModel.refreshState.observe(this) {
            swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
            if (it?.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
        viewModel.networkState.observe(this, adapter.networkStateObserver)
        viewModel.networkState.observe(this) {
            it ?: return@observe
            progress.visibleOrGone(it == NetworkState.INITIAL_LOADING)

            if (it.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
    }
}
