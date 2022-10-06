package com.example.newsapp.ui.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.ui.adapters.NewsAdapter
import com.example.newsapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()
    lateinit var searchAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initViewModel()
        job()
    }

    private fun initAdapter() {
        searchAdapter = NewsAdapter()
        binding.rvSearchFragment.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initViewModel() {
        viewModel.searchNewsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBarSearchFragment.visibility = View.INVISIBLE
                    response.data?.let {
                        searchAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error -> {
                    binding.progressBarSearchFragment.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.e("checkData", "MainFragment : error: ${it}")
                    }
                }
                is Resource.Loading -> {
                    binding.progressBarSearchFragment.visibility = View.VISIBLE

                }
            }
        }
    }

    fun job() {
        var job: Job? = null
        binding.edSearchFragment.addTextChangedListener { text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                text?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.getSearchNews(query = it.toString())
                    }
                }
            }
        }
    }
}