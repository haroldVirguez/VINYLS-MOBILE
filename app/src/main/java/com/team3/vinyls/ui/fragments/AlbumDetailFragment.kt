package com.team3.vinyls.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.app.AlertDialog
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.team3.vinyls.R
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.data.repositories.TrackRepository
import com.team3.vinyls.data.services.AlbumsService
import com.team3.vinyls.data.services.TrackService
import com.team3.vinyls.viewmodels.AlbumDetailViewModel
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.databinding.FragmentAlbumDetailBinding
import com.bumptech.glide.Glide
import androidx.core.net.toUri
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private val args: AlbumDetailFragmentArgs by navArgs()

    private val viewModel: AlbumDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)

                val albumService = retrofit.create(AlbumsService::class.java)
                val albumRepository = AlbumRepository(albumService)

                val trackService = retrofit.create(TrackService::class.java)
                val trackRepository = TrackRepository(trackService)

                return AlbumDetailViewModel(albumRepository, trackRepository) as T
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
        val fabAddTrack =
            view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.fabAddTrack
            )

        fabAddTrack.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.menu_fab_options, null)
            val option1 = dialogView.findViewById<TextView>(R.id.option1)
            val option2 = dialogView.findViewById<TextView>(R.id.option2)

            option1.text = "Agregar canción"
            option2.text = "Agregar comentario"

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawable(
                android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            )
            dialog.show()

            option1.setOnClickListener {
                showAddTrackDialog(albumId)
                dialog.dismiss()
            }

            option2.setOnClickListener {
                showAddCommentDialog()
                dialog.dismiss()
            }
        }

        viewModel.loadAlbumDetail(albumId)
        viewModel.loadTracks(albumId)

        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.txtTitle.text = album.name
            binding.txtSubtitle.text = "${album.genre} • ${album.recordLabel}"
            binding.txtDescription.text = album.description
            binding.txtReleaseDate.text = "Lanzado en ${album.releaseDate.take(4)}"
            // Cover
            Glide.with(requireContext())
                .load(album.cover.toUri().buildUpon().scheme("https").build())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_broken_image)
                )
                .centerCrop()
                .into(binding.imgAlbumCover)

            // Comentarios
            val commentsText =
                album.comments?.joinToString("\n\n") { "⭐️ ${it.rating}/5\n${it.description}" }
                    ?: "Sin comentarios aún"
            binding.txtComments.text = commentsText
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            binding.tracksContainer.removeAllViews()

            tracks.forEachIndexed { index, track ->
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
                    text = track.duration
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                    textSize = 14f
                }

                row.addView(numberView)
                row.addView(nameView)
                row.addView(durationView)

                binding.tracksContainer.addView(row)
            }
        }
    }

    private fun showAddTrackDialog(albumId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_track, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.inputTrackName)
        val durationInput = dialogView.findViewById<TextInputEditText>(R.id.inputTrackDuration)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
        )
        dialog.show()

        val btnSave =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnCancel =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val trackName = nameInput.text.toString().trim()
            val trackDuration = durationInput.text.toString().trim()

            if (trackName.isNotEmpty() && trackDuration.isNotEmpty()) {
                viewModel.addTrackToAlbum(albumId, trackName, trackDuration)
                viewModel.loadTracks(albumId)
                Toast.makeText(
                    requireContext(),
                    "Canción agregada",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showAddCommentDialog() {
        // TODO: Implementar flujo real de agregar comentario
        Toast.makeText(
            requireContext(),
            "Agregar comentario (pendiente de implementar)",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
