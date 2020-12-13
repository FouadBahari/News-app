package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.*
import com.androiddevs.mvvmnewsapp.model.Article


@Database(
    entities = [Article::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class ArticleDataBase :RoomDatabase(){

    abstract fun getArticleDao():ArticleDAO

    companion object {
        @Volatile
        private var instance : ArticleDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabse(context).also {
                instance = it
            }
        }

        private fun createDatabse(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDataBase::class.java,
                "articles.db"
        ).fallbackToDestructiveMigration().build()
    }
}