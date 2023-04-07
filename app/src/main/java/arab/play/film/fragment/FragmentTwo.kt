package arab.play.film.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import arab.play.film.MainActivity
import arab.play.film.R

class FragmentTwo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mFragmentTwo = LayoutInflater.from(this@FragmentTwo.requireContext()).inflate(R.layout.fragment_two,container,false)
        mFragmentTwo.findViewById<FrameLayout>(R.id.btnHome).setOnClickListener {
            val intent =Intent(this@FragmentTwo.requireContext(),MainActivity::class.java)
            this@FragmentTwo.requireContext().startActivity(intent)
        }
        return mFragmentTwo
    }

}