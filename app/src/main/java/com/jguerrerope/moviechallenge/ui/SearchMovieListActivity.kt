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
import com.jguerrerope.moviechallenge.extension.textWatcherOnArfterTextChanged
import com.jguerrerope.moviechallenge.extension.visibleOrGone
import com.jguerrerope.moviechallenge.ui.adapter.MoviePagedListAdapter
import com.jguerrerope.moviechallenge.ui.viewmodel.MoviePopularViewModel
import com.jguerrerope.moviechallenge.ui.viewmodel.SearchMovieViewModel
import kotlinx.android.synthetic.main.activity_search_movie_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class SearchMovieListActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var adapter: MoviePagedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_slide_up, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie_list)
        setUpViews()
        setUpViewModels()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.transition_no_animation, R.anim.transition_slide_down)
    }

    private fun setUpViews() {
        adapter = MoviePagedListAdapter {
            startActivity<MovieDetailsActivity>(Pair(MovieDetailsActivity.EXTRA_MOVIE, it))
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchMovieListActivity)
            adapter = this@SearchMovieListActivity.adapter
        }
        search.textWatcherOnArfterTextChanged {
            viewModel.onSearchInputStateChanged(search.text.toString())
        }
        cancelButton.setOnClickListener { finish() }
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchMovieViewModel::class.java)
        viewModel.searchMovies.observe(this) {
            adapter.submitList(it)
        }

        viewModel.networkState.observe(this, adapter.networkStateObserver)
        viewModel.networkState.observe(this) {
            it ?: return@observe
            progress.visibleOrGone(it == NetworkState.INITIAL_LOADING)

//            if (it.status == Status.FAILED) {
//                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_LONG)
//                        .setAction(R.string.retry) { viewModel.retry() }
//                        .show()
//            }
        }
    }
}
