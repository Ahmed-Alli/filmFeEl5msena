package arab.play.film

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import arab.play.film.adapter.ViewAdapter
import arab.play.film.fragment.FragmentOne
import arab.play.film.fragment.FragmentTwo

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val rotate = AnimationUtils.loadAnimation(this@WelcomeActivity,R.anim.rotate)
        val roll2 = findViewById<ImageView>(R.id.imgRoll2)
        roll2.startAnimation(rotate)
        viewSetup()

    }

    private fun viewSetup() {
        val adapter = ViewAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val rotate = AnimationUtils.loadAnimation(this@WelcomeActivity,R.anim.rotate)
        val roll2 = findViewById<ImageView>(R.id.imgRoll2)
        adapter.addFragment(FragmentOne(),"")
        adapter.addFragment(FragmentTwo(),"")
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                when(position){
                    0->{
                        roll2.clearAnimation()
                    }
                   1->{
                        roll2.startAnimation(rotate)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        roll2.setOnClickListener {
            viewPager.currentItem=1
        }

    }
}