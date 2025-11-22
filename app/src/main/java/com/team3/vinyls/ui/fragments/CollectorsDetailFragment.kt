package com.team3.vinyls.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.team3.vinyls.R
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.data.repositories.CollectorRepository
import com.team3.vinyls.data.services.CollectorsService
import com.team3.vinyls.databinding.FragmentCollectorsDetailBinding
import com.team3.vinyls.ui.AvatarUtils
import com.team3.vinyls.viewmodels.CollectorsDetailViewModel

class CollectorsDetailFragment : Fragment() {

    private var _binding: FragmentCollectorsDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CollectorsDetailFragmentArgs by navArgs()

    private val viewModel: CollectorsDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(CollectorsService::class.java)
                val repo = CollectorRepository(service)
                return CollectorsDetailViewModel(repo) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCollectorsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectorId = args.collectorId.toInt()

        setupObservers()

        viewModel.loadCollectorDetail(collectorId)
    }

    private fun setupObservers() {

        viewModel.collector.observe(viewLifecycleOwner) { collector ->
            if (collector != null) {
                bindCollector(collector)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.txtCollectorName.text = "Error: $error"
            }
        }
    }

    private fun truncate(text: String?, limit: Int = 100): String {
        if (text == null) return ""
        return if (text.length > limit) text.take(limit) + "…" else text
    }

    private fun bindCollector(data: CollectorDto) {


        binding.txtCollectorName.text = data.name


        AvatarUtils.setInitialsAvatar(binding.imgCollectorAvatar, data.name, data.id)


        binding.txtTelephoneValue.text = data.telephone ?: "No disponible"
        binding.txtEmailValue.text = data.email ?: "No disponible"


        binding.commentsContainer.removeAllViews()
        data.comments?.forEach { comment ->
            val tv = TextView(requireContext()).apply {
                text = "${comment.rating} ⭐:  ${comment.description}"
                setTextColor(resources.getColor(R.color.text_secondary, null))
                textSize = 14f
                setPadding(0, 8, 0, 8)
            }
            binding.commentsContainer.addView(tv)
        }


        binding.performersContainer.removeAllViews()
        val layoutId =  R.layout.item_performer_small
        data.favoritePerformers?.forEach { performer ->

            val view = layoutInflater.inflate(layoutId, binding.performersContainer, false)

            val img = view.findViewById<android.widget.ImageView>(R.id.imgPerformer)
            val name = view.findViewById<TextView>(R.id.txtPerformerName)
            val desc = view.findViewById<TextView>(R.id.txtPerformerDescription)

            Glide.with(this)
                .load(performer.image)
                .placeholder(R.drawable.ic_broken_image)
                .into(img)

            name.text = performer.name
            desc.text = truncate(performer.description, 100)

            binding.performersContainer.addView(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}