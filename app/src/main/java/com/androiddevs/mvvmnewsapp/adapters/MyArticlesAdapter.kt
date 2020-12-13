package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.model.Article
import com.androiddevs.mvvmnewsapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

class MyArticlesAdapter: RecyclerView.Adapter<MyArticlesAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url

        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = differ.currentList.get(position)
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvDescription.text = article.description
            tvTitle.text = article.title
            tvSource.text = article.source?.name
            tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit) ? = null

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}