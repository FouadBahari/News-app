package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.MyArticlesAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var myArticlesAdapter: MyArticlesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel

        setupRecyclerview()

        myArticlesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = myArticlesAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Article deleted successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("undo"){
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }

            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        newsViewModel.getSavedNews().observe(viewLifecycleOwner, Observer {articles ->
            myArticlesAdapter.differ.submitList(articles)
        })
    }



    private fun setupRecyclerview(){
        myArticlesAdapter = MyArticlesAdapter()
        rvSavedNews.apply {
            adapter = myArticlesAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }
}