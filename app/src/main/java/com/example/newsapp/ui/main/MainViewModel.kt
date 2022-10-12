package com.example.newsapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.api.NewsRepository
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsResponse
import com.example.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {


    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val newsPage = 1

    init {
        getNews("ru")
    }

    private fun getNews(countryCode: String) =
        viewModelScope.launch {
            newsLiveData.postValue(Resource.Loading())
            val response = repository.getNews(countryCode = countryCode, pageNumber = newsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))
                }
            } else {
                newsLiveData.postValue(Resource.Error(message = response.message()))
            }
        }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.addToFavorite(article = article)
    }

    fun getSaveArticle() = repository.getFavoritesArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteFromFavorite(article = article)
    }
}