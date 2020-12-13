package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.Utils.Contants
import com.androiddevs.mvvmnewsapp.Utils.Contants.Companion.SEARCH_NEWS_TIME_DELAY
import com.androiddevs.mvvmnewsapp.Utils.Resource
import com.androiddevs.mvvmnewsapp.adapters.MyArticlesAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var myArticlesAdapter: MyArticlesAdapter

    val TAG = "SearchNewsfragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel

        setupRecyclerview()

        myArticlesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        newsViewModel.searchNews(editable.toString())
                    }
                }

            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        myArticlesAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Contants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if (isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity,"An error occured $message", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()

                }

            }
        })

    }


    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Contants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotAtBeginning && isAtLastItem && isNotLoadingAndNotLastPage
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                newsViewModel.searchNews(etSearch.text.toString())

                isScrolling = false
            }
        }
    }


    private fun setupRecyclerview(){
        myArticlesAdapter = MyArticlesAdapter()
        rvSearchNews.apply {
            adapter = myArticlesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

}