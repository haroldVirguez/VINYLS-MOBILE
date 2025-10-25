package com.team3.vinyls.albums.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.team3.vinyls.databinding.FragmentAlbumsBinding
import com.team3.vinyls.albums.viewmodels.AlbumsViewModel
import androidx.navigation.fragment.findNavController
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.albums.data.AlbumsService
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.albums.ui.adapters.AlbumsAdapter

class AlbumsFragment : Fragment() {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(AlbumsService::class.java)
                val repository = AlbumRepository(service)
                return AlbumsViewModel(repository) as T
            }
        }
    }
    private val adapter = AlbumsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerAlbums.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAlbums.adapter = adapter

        viewModel.albums.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
            }
        }

        adapter.onAlbumClick = { album ->
            val action = AlbumsFragmentDirections.actionAlbumsFragmentToAlbumDetailFragment(album.id.toString())
            findNavController().navigate(action)
        }

        binding.btnViewAll.setOnClickListener {
            viewModel.refresh()
        }
    }
}
