package app.simple.inure.ui.viewers

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.simple.inure.R
import app.simple.inure.constants.ServiceConstants
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.theme.ThemeMaterialCardView
import app.simple.inure.decorations.theme.ThemeSeekBar
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.extension.fragments.ScopedBottomSheetFragment
import app.simple.inure.glide.util.AudioCoverUtil.loadFromFileDescriptor
import app.simple.inure.preferences.AppearancePreferences
import app.simple.inure.services.AudioService
import app.simple.inure.util.NumberUtils
import app.simple.inure.util.ViewUtils

class AudioPlayer : ScopedBottomSheetFragment() {

    private lateinit var art: ImageView
    private lateinit var playPause: DynamicRippleImageButton
    private lateinit var duration: TypeFaceTextView
    private lateinit var progress: TypeFaceTextView
    private lateinit var title: TypeFaceTextView
    private lateinit var artist: TypeFaceTextView
    private lateinit var album: TypeFaceTextView
    private lateinit var fileInfo: TypeFaceTextView
    private lateinit var playerContainer: ThemeMaterialCardView
    private lateinit var seekBar: ThemeSeekBar

    private var animation: ObjectAnimator? = null
    private var uri: Uri? = null
    private var audioService: AudioService? = null
    private var serviceConnection: ServiceConnection? = null
    private var audioBroadcastReceiver: BroadcastReceiver? = null

    private val audioIntentFilter = IntentFilter()
    private var serviceBound = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_audio_player, container, false)

        art = view.findViewById(R.id.album_art_mime)
        playPause = view.findViewById(R.id.mime_play_button)
        duration = view.findViewById(R.id.current_duration_mime)
        progress = view.findViewById(R.id.current_time_mime)
        fileInfo = view.findViewById(R.id.mime_info)
        title = view.findViewById(R.id.mime_title)
        artist = view.findViewById(R.id.mime_artist)
        album = view.findViewById(R.id.mime_album)
        seekBar = view.findViewById(R.id.seekbar_mime)
        playerContainer = view.findViewById(R.id.container)

        uri = requireArguments().getParcelable("uri")!!
        audioIntentFilter.addAction(ServiceConstants.actionPrepared)
        audioIntentFilter.addAction(ServiceConstants.actionQuitService)
        audioIntentFilter.addAction(ServiceConstants.actionMetaData)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerContainer.radius = AppearancePreferences.getCornerRadius().toFloat()
        ViewUtils.addShadow(playerContainer)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                serviceBound = true
                audioService = (service as AudioService.AudioBinder).getService()
                audioService?.audioUri = uri
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                serviceBound = false
            }
        }

        audioBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ServiceConstants.actionPrepared -> {
                        seekBar.max = audioService?.getDuration()!!
                        duration.text = NumberUtils.getFormattedTime(audioService?.getDuration()?.toLong()!!)

                        handler.post(progressRunnable)
                    }
                    ServiceConstants.actionMetaData -> {
                        title.text = audioService?.metaData?.title
                        artist.text = audioService?.metaData?.artists
                        album.text = audioService?.metaData?.album
                        fileInfo.text = getString(R.string.audio_file_info, audioService?.metaData?.format, audioService?.metaData?.sampling, audioService?.metaData?.bitrate)
                        art.loadFromFileDescriptor(uri!!)
                    }
                    ServiceConstants.actionQuitService -> {
                        requireContext().unbindService(serviceConnection!!)
                        requireActivity().finishAfterTransition()
                    }
                }
            }
        }

        art.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    art.animate()
                        .scaleX(0.8F)
                        .scaleY(0.8F)
                        .setInterpolator(DecelerateInterpolator(1.5F))
                        .start()
                }
                MotionEvent.ACTION_UP -> {
                    art.animate()
                        .scaleX(1.0F)
                        .scaleY(1.0F)
                        .setInterpolator(DecelerateInterpolator(1.5F))
                        .start()

                    kotlin.runCatching {
                        (art.drawable as AnimatedVectorDrawable).start()
                    }.getOrElse {
                        it.printStackTrace()
                    }
                }
            }

            true
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    this@AudioPlayer.progress.text = NumberUtils.getFormattedTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                animation?.cancel()
                handler.removeCallbacks(progressRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                audioService?.seek(seekBar.progress)
                handler.post(progressRunnable)
            }
        })

        playPause.setOnClickListener {
            buttonStatus(audioService?.changePlayerState()!!)
        }

        playerContainer.setOnClickListener {
            buttonStatus(audioService?.changePlayerState()!!)
        }
    }

    private fun setSeekbarProgress(seekbarProgress: Int) {
        animation = ObjectAnimator.ofInt(seekBar, "progress", seekbarProgress)
        animation!!.duration = 500L
        animation!!.interpolator = LinearOutSlowInInterpolator()
        animation!!.start()
    }

    private fun buttonStatus(isPlaying: Boolean) {
        if (isPlaying) {
            playPause.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, requireContext().theme))
        } else {
            playPause.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_play, requireContext().theme))
        }
    }

    private val progressRunnable: Runnable = object : Runnable {
        override fun run() {
            setSeekbarProgress(audioService?.getProgress()!!)
            progress.text = NumberUtils.getFormattedTime(audioService?.getProgress()!!.toLong())
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireActivity(), AudioService::class.java)
        requireContext().startService(intent)
        serviceConnection?.let { requireContext().bindService(intent, it, Context.BIND_AUTO_CREATE) }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(audioBroadcastReceiver!!, audioIntentFilter)
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            serviceConnection?.let { requireContext().unbindService(it) }
        }
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(audioBroadcastReceiver!!)
    }

    companion object {
        fun newInstance(uri: Uri): AudioPlayer {
            val args = Bundle()
            args.putParcelable("uri", uri)
            val fragment = AudioPlayer()
            fragment.arguments = args
            return fragment
        }
    }
}