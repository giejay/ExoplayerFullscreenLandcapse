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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.fernandocejas.sample.R
import com.fernandocejas.sample.core.extension.inTransaction
import com.fernandocejas.sample.core.platform.BaseFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspDefaultClient
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.source.rtsp.core.Client
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.google.android.exoplayer2.ExoPlayerFactory

import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_layout.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.main_layout


class LoginFragment : BaseFragment() {

    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
    private val TAG = "ExoPlayer"

    private lateinit var dataSourceFactory: DefaultHttpDataSourceFactory
    private lateinit var exoPlayer: ExoPlayer
    private val source: MediaSource = RtspMediaSource.Factory(
        RtspDefaultClient.factory()
            .setFlags(Client.FLAG_ENABLE_RTCP_SUPPORT)
            .setNatMethod(Client.RTSP_NAT_DUMMY)
    )
        .createMediaSource(Uri.parse("rtsp://192.168.2.15:8554/proxied"))

    //    private lateinit var mFullScreenIcon: ImageView
    private lateinit var mFullScreenDialog: Dialog
//    private lateinit var frameLayout: FrameLayout
//    private lateinit var mFullScreenButton: FrameLayout

    private var mExoPlayerFullscreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        childFragmentManager.inTransaction {
            add(
                R.id.historyFragment,
                HistoryFragment.newInstance("1", "2")
            )
        }

        val context = requireContext()
        val trackSelector = DefaultTrackSelector()
        val renderersFactory: RenderersFactory = DefaultRenderersFactory(context)
        this.exoPlayer =
            ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector)
        this.dataSourceFactory =
            DefaultHttpDataSourceFactory("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
        prepare()

        savedInstanceState?.getBoolean(STATE_PLAYER_FULLSCREEN)?.let {
            mExoPlayerFullscreen = it
        }

        mFullScreenDialog =
            object : Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exo_play.setOnClickListener {
            prepare()
        }
        exo_fullscreen_button.setOnClickListener { if (!mExoPlayerFullscreen) openFullscreenDialog() else closeFullscreenDialog() }
        exoPlayerView!!.useController = true;
        exoPlayerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
        exoPlayerView!!.player = this.exoPlayer
        this.exoPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        Snackbar.make(main_layout, "Player is idle, retrying", Snackbar.LENGTH_SHORT).show()
                        prepare()
                        Log.d(TAG, "onPlayerStateChanged: STATE_IDLE")
                    }
                    Player.STATE_BUFFERING -> {
                        Snackbar.make(main_layout, "Player is buffering", Snackbar.LENGTH_INDEFINITE).show()
                        Log.d(TAG, "onPlayerStateChanged: STATE_BUFFERING")
                    }
                    Player.STATE_READY -> {
                        Snackbar.make(main_layout, "Player is ready", Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "onPlayerStateChanged: STATE_READY")
                    }
                    Player.STATE_ENDED -> {
                        Snackbar.make(main_layout, "Player is stopped, retrying", Snackbar.LENGTH_SHORT).show()
                        prepare()
                        Log.d(TAG, "onPlayerStateChanged: STATE_ENDED")
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
//        outState.putLong(STATE_RESUME_POSITION, mResumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen)
        super.onSaveInstanceState(outState)
    }

    private fun prepare() {
        exoPlayer.prepare(source)
        exoPlayer.playWhenReady = true
    }

    override fun onResume() {
        super.onResume()
        if (mExoPlayerFullscreen && !mFullScreenDialog.isShowing) {
            enableFullScreen()
        }
    }

    private fun openFullscreenDialog() {
        mExoPlayerFullscreen = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableFullScreen()
    }

    private fun enableFullScreen() {
        main_media_frame.removeView(exoPlayerView)
        mFullScreenDialog.addContentView(
            exoPlayerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        (exo_fullscreen_button.getChildAt(0) as ImageView).setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_fullscreen_skrink
            )
        )
        mFullScreenDialog.show()
    }

    private fun closeFullscreenDialog() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (exoPlayerView.parent as ViewGroup).removeView(exoPlayerView)
        main_media_frame.addView(exoPlayerView)
        mExoPlayerFullscreen = false
        mFullScreenDialog.dismiss()
        (exo_fullscreen_button.getChildAt(0) as ImageView).setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_fullscreen_expand
            )
        )
    }

    override fun layoutId() = R.layout.fragment_login
}
