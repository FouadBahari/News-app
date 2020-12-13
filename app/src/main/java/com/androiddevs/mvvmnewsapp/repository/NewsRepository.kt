package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDataBase
import com.androiddevs.mvvmnewsapp.model.Article

class NewsRepository (
    val db : ArticleDataBase
){
    suspend fun getBreakingNews(countryCode: String,pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String , pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteSavedNews(article: Article) = db.getArticleDao().deleteArticle(article)


}

