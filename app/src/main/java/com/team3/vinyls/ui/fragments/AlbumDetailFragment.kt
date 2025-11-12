package com.team3.vinyls.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.navArgs
import com.team3.vinyls.R
import com.team3.vinyls.data.repository.AlbumRepository
import com.team3.vinyls.data.service.AlbumsService
import com.team3.vinyls.viewmodels.AlbumDetailViewModel
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.databinding.FragmentAlbumDetailBinding
import java.net.URL

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private val args: AlbumDetailFragmentArgs by navArgs()

    private val viewModel: AlbumDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(AlbumsService::class.java)
                val repository = AlbumRepository(service)
                return AlbumDetailViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = args.albumId.toInt()
        viewModel.loadAlbumDetail(albumId)

        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.txtTitle.text = album.name
            binding.txtSubtitle.text = "${album.genre} • ${album.recordLabel}"
            binding.txtDescription.text = album.description
            binding.txtReleaseDate.text = "Lanzado en ${album.releaseDate.take(4)}"

            // Cover
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        val url = URL(album.cover)
                        BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    }
                    binding.imgAlbumCover.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Tracks
            binding.tracksContainer.removeAllViews()

            album.tracks?.forEachIndexed { index, track ->
                val row = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 6, 0, 6)
                }

                val numberView = TextView(requireContext()).apply {
                    text = "${index + 1}."
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                    textSize = 14f
                    setPadding(0, 0, 8, 0)
                }

                val nameView = TextView(requireContext()).apply {
                    text = track.name
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    textSize = 14f
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }

                val durationView = TextView(requireContext()).apply {
                    text = "${track.duration} min"
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                    textSize = 14f
                }

                row.addView(numberView)
                row.addView(nameView)
                row.addView(durationView)

                binding.tracksContainer.addView(row)
            }

            // Comentarios
            val commentsText =
                album.comments?.joinToString("\n\n") { "⭐️ ${it.rating}/5\n${it.description}" }
                    ?: "Sin comentarios aún"
            binding.txtComments.text = commentsText
        }

        binding.btnAddComment.setOnClickListener {
            // Aquí más adelante podrás abrir un formulario o diálogo para agregar comentario
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
