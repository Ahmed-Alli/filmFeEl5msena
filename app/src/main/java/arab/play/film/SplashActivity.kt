package arab.play.film

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import arab.play.film.ads.MyApplication


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private  val COUNTER_TIME = 6L
    private  val LOG_TAG = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val logo = findViewById<ImageView>(R.id.imgLogo)

        createTimer(COUNTER_TIME)
        val tr1 = AnimationUtils.loadAnimation(this,R.anim.splash_1)
        val rotate = AnimationUtils.loadAnimation(this,R.anim.rotate)

        val roll1 = findViewById<ImageView>(R.id.imgRoll1)
        val roll2 = findViewById<ImageView>(R.id.imgRoll2)
        val roll3 = findViewById<ImageView>(R.id.imgRoll3)

        roll1.startAnimation(rotate)
        roll2.startAnimation(rotate)
        roll3.startAnimation(rotate)


        animateView(logo)



    }
    private fun createTimer(seconds: Long) {
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                val application = application as? MyApplication
                if (application == null) {
                    Log.e(LOG_TAG, "Failed to cast application to MyApplication.")
                    startMainActivity()
                    return
                }
                application.showAdIfAvailable(
                    this@SplashActivity,
                    object : MyApplication.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            startMainActivity()
                        }
                    })
            }
        }
        countDownTimer.start()
    }
    fun startMainActivity() {
        val intent = Intent(this,WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun animateView(view: ImageView) {
        when (val drawable = view.drawable) {
            is AnimatedVectorDrawableCompat -> {
                drawable.start()
            }
            is AnimatedVectorDrawable -> {
                drawable.start()
            }
        }
    }
}