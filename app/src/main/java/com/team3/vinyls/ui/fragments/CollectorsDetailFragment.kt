package com.team3.vinyls.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.data.repositories.CollectorRepository
import com.team3.vinyls.data.services.CollectorsService
import com.team3.vinyls.databinding.FragmentCollectorsDetailBinding
import com.team3.vinyls.viewmodels.CollectorsDetailViewModel
import kotlin.getValue

class CollectorsDetailFragment : Fragment() {

    private var _binding: FragmentCollectorsDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CollectorsDetailFragmentArgs by navArgs()

    private val viewModel: CollectorsDetailViewModel by viewModels {
        object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val collectorService = retrofit.create(CollectorsService::class.java)
                val collectorRepository = CollectorRepository(collectorService)

                return CollectorsDetailViewModel(collectorRepository) as T

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollectorsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectorId = args.collectorId.toInt()

        viewModel.loadCollectorDetail(collectorId)
    }

}