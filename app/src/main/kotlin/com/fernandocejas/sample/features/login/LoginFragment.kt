/**
 * Copyright (C) 2020 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.sample.features.login

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.fernandocejas.sample.R
import com.fernandocejas.sample.core.platform.BaseFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspDefaultClient
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.source.rtsp.core.Client
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.google.android.exoplayer2.ExoPlayerFactory

import com.google.android.exoplayer2.ui.PlayerControlView
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"

    private lateinit var dataSourceFactory: DefaultHttpDataSourceFactory
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var mFullScreenIcon: ImageView
    private lateinit var mFullScreenDialog: Dialog
    private lateinit var frameLayout: FrameLayout
    private lateinit var mFullScreenButton: FrameLayout

    private var mExoPlayerFullscreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        playerView = view.findViewById(R.id.exoplayer)
        frameLayout = view.findViewById(R.id.main_media_frame)

        val context = requireContext()
        val trackSelector = DefaultTrackSelector()
        val renderersFactory: RenderersFactory = DefaultRenderersFactory(context)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector)

//        val userAgent = context.resources.getString(R.string.user_agent)
        this.dataSourceFactory =
            DefaultHttpDataSourceFactory("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")

        playerView.useController = true;
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
        playerView.setPlayer(this.exoPlayer);

        savedInstanceState?.getBoolean(STATE_PLAYER_FULLSCREEN)?.let {
            mExoPlayerFullscreen = it
        }

        prepare()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
//        outState.putLong(STATE_RESUME_POSITION, mResumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen)
        super.onSaveInstanceState(outState)
    }

    private fun prepare() {
        val uri = Uri.parse("rtsp://192.168.2.15:8554/proxied")
        val source: MediaSource = if (Util.isRtspUri(uri)) {
            RtspMediaSource.Factory(
                RtspDefaultClient.factory()
                    .setFlags(Client.FLAG_ENABLE_RTCP_SUPPORT)
                    .setNatMethod(Client.RTSP_NAT_DUMMY)
            )
                .createMediaSource(uri)
        } else {
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        }
        exoPlayer.prepare(source)
        exoPlayer.playWhenReady = true
        initFullscreenButton()
        initFullscreenDialog()
    }

    override fun onResume() {
        super.onResume()
        initExoPlayer()
        if (mExoPlayerFullscreen) {
            frameLayout.removeView(playerView)
            mFullScreenDialog.addContentView(
                playerView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            mFullScreenIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this.requireContext(),
                    R.drawable.ic_fullscreen_skrink
                )
            )
            mFullScreenDialog.show()
        }
    }

    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }


    private fun openFullscreenDialog() {
        mExoPlayerFullscreen = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        frameLayout.removeView(playerView)
        mFullScreenDialog.addContentView(
            playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_fullscreen_skrink
            )
        )
        mFullScreenDialog.show()
    }


    private fun closeFullscreenDialog() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        (playerView.parent as ViewGroup).removeView(playerView)
//        frameLayout.addView(playerView)
        mExoPlayerFullscreen = false
        mFullScreenDialog.dismiss()
        mFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_fullscreen_expand
            )
        )
    }

    private fun initFullscreenButton() {
        val controlView: PlayerControlView = playerView.findViewById(R.id.exo_controller)
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon)
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button)
        mFullScreenButton.setOnClickListener { if (!mExoPlayerFullscreen) openFullscreenDialog() else closeFullscreenDialog() }
    }

    private fun initExoPlayer() {
//        val haveResumePosition = mResumeWindow !== C.INDEX_UNSET
//        if (haveResumePosition) {
//            Log.i("DEBUG", " haveResumePosition ")
//            AudioPlayer.player.seekTo(mResumeWindow, mResumePosition)
//        }
//        val contentUrl = getString(R.string.content_url)
//        mVideoSource = buildMediaSource(Uri.parse(contentUrl))
//        Log.i("DEBUG", " mVideoSource $mVideoSource")
//        AudioPlayer.player.prepare(mVideoSource)
//        AudioPlayer.player.setPlayWhenReady(true)
    }


    override fun layoutId() = R.layout.fragment_login
}
