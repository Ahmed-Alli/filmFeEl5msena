package arab.play.film

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import arab.play.film.databinding.ActivityYoutubeBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class YouTubeActivity : AppCompatActivity(),YouTubePlayerListener {
    private lateinit var youTubePlayer: YouTubePlayer
    private lateinit var binding: ActivityYoutubeBinding
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var isPIPMode = false

    private var isFullscreen = false
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                // if the player is in fullscreen, exit fullscreen
                youTubePlayer.toggleFullscreen()
            } else {
                finish()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        youTubePlayerView = findViewById(R.id.youtubePlayerView)
        val fullscreenViewContainer = findViewById<FrameLayout>(R.id.full_screen_view_container)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.enableAutomaticInitialization = false

        val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1)
            .build()



        youTubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullscreen = true

                // the video will continue playing in fullscreenView
                youTubePlayerView.visibility = View.GONE
                binding.pip.visibility =View.GONE
                binding.appbar.visibility = View.GONE
                binding.linear.visibility = View.GONE
                binding.name.visibility = View.GONE
                binding.name.visibility = View.GONE
                binding.btnHome.visibility = View.GONE
                binding.category.visibility = View.GONE
                binding.story.visibility = View.GONE
                binding.textView19.visibility = View.GONE
                binding.textView20.visibility = View.GONE
                binding.imageView3.visibility = View.GONE
                supportActionBar?.hide()

                // Make the activity fullscreen
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )

                fullscreenViewContainer.visibility = View.GONE

                fullscreenViewContainer.visibility = View.VISIBLE
                fullscreenViewContainer.addView(fullscreenView)

                // optionally request landscape orientation
                // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {
                isFullscreen = false
                supportActionBar?.show()
                // the video will continue playing in the player
                youTubePlayerView.visibility = View.VISIBLE

                binding.pip.visibility =View.VISIBLE
                binding.appbar.visibility = View.VISIBLE
                binding.linear.visibility = View.VISIBLE
                binding.name.visibility = View.VISIBLE
                binding.name.visibility = View.VISIBLE
                binding.btnHome.visibility = View.VISIBLE
                binding.category.visibility = View.VISIBLE
                binding.story.visibility = View.VISIBLE
                binding.textView19.visibility = View.VISIBLE
                binding.textView20.visibility = View.VISIBLE
                binding.imageView3.visibility = View.VISIBLE


                fullscreenViewContainer.removeAllViews()


            }
        })

        lifecycle.addObserver(youTubePlayerView)

        //  val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        youTubePlayerView.initialize(this,iFramePlayerOptions)
        val btnHome = findViewById<Button>(R.id.btnHome)
        btnHome.setOnClickListener {
            onBackPressed()
        }



        binding.pip.setOnClickListener {
            onUserLeaveHint()
        }


        getData()
        getVideo()

        youTubePlayerView.addOnLayoutChangeListener {
                _, left, top, right, bottom,
                oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft
                || right != oldRight
                || top != oldTop
                || bottom != oldBottom) {
                // The playerView's bounds changed, update the source hint rect to
                // reflect its new bounds.
                val sourceRectHint = Rect()

                youTubePlayerView.getGlobalVisibleRect(sourceRectHint)
                setPictureInPictureParams(
                    PictureInPictureParams.Builder()
                        .setSourceRectHint(sourceRectHint)
                        .build()

                )
            }
        }


        val mOnLayoutChangeListener = View.OnLayoutChangeListener {
                v: View?, oldLeft: Int,
                oldTop: Int, oldRight: Int,
                oldBottom: Int, newLeft: Int, newTop:
                Int, newRight: Int, newBottom: Int ->
            val sourceRectHint = Rect()
            youTubePlayerView.getGlobalVisibleRect(sourceRectHint)
            val builder = PictureInPictureParams.Builder()
                .setSourceRectHint(sourceRectHint)
            setPictureInPictureParams(builder.build())

        }

        youTubePlayerView.addOnLayoutChangeListener(mOnLayoutChangeListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setPictureInPictureParams(PictureInPictureParams.Builder()
                .setSeamlessResizeEnabled(false)
                .setAutoEnterEnabled(true)
                .build())

        }
    }

    override fun onUserLeaveHint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isPIPMode) {
            enterPictureInPictureMode()
        }
        super.onUserLeaveHint()


    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isPIPMode = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            binding.pip.visibility =View.GONE
            binding.appbar.visibility = View.GONE
            binding.linear.visibility = View.GONE
            binding.name.visibility = View.GONE
            binding.name.visibility = View.GONE
            binding.btnHome.visibility = View.GONE
            binding.category.visibility = View.GONE
            binding.story.visibility = View.GONE
            binding.textView19.visibility = View.GONE
            binding.textView20.visibility = View.GONE
            binding.imageView3.visibility = View.GONE
        } else {
            binding.pip.visibility =View.VISIBLE
            binding.appbar.visibility = View.VISIBLE
            binding.linear.visibility = View.VISIBLE
            binding.name.visibility = View.VISIBLE
            binding.name.visibility = View.VISIBLE
            binding.btnHome.visibility = View.VISIBLE
            binding.category.visibility = View.VISIBLE
            binding.story.visibility = View.VISIBLE
            binding.textView19.visibility = View.VISIBLE
            binding.textView20.visibility = View.VISIBLE
            binding.imageView3.visibility = View.VISIBLE
        }
    }
    private fun getData() {
        val name = findViewById<TextView>(R.id.name)
        val rate = findViewById<TextView>(R.id.rate)
        val category = findViewById<TextView>(R.id.category)
        val story = findViewById<TextView>(R.id.story)
        val fName = intent.getStringExtra("name")
        val fCategory = intent.getStringExtra("category")
        val fRate = intent.getStringExtra("rate")
        val fStory = intent.getStringExtra("story")
        name.text = fName
        rate.text = fRate
        category.text = fCategory
        story.text = fStory

    }
    private fun getVideo() {

    }

    override fun onApiChange(youTubePlayer: YouTubePlayer) {
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
    }

    override fun onPlaybackQualityChange(
        youTubePlayer: YouTubePlayer,
        playbackQuality: PlayerConstants.PlaybackQuality
    ) {
    }

    override fun onPlaybackRateChange(
        youTubePlayer: YouTubePlayer,
        playbackRate: PlayerConstants.PlaybackRate
    ) {
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {

        val videoId = intent.getStringExtra("videoId")
        if (videoId != null) {
            youTubePlayer.loadVideo(videoId, 0F)
        }

    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {

    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {

    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
    }

    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {

    }


}
