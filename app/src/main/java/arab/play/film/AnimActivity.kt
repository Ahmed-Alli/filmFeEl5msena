package arab.play.film

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.adapter.AnimAdapter
import arab.play.film.adapter.AnimAdapterSearch
import arab.play.film.adapter.TvShowAdapter
import arab.play.film.apiActivity.AnimItem
import arab.play.film.apiActivity.TvShowItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.database.*
import java.util.*

class AnimActivity : AppCompatActivity() {

    private val animItem = AnimItem::class.java
    private lateinit var animRecyclerView: RecyclerView
    private lateinit var dbref: DatabaseReference
    private lateinit var animArrayList: ArrayList<AnimItem>
    private lateinit var searchView: SearchView



    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false
    private val adSize: AdSize
        get() {
            return AdSize.BANNER
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)

        searchView = findViewById(R.id.searchView)

        animRecyclerView = findViewById(R.id.rv)

        animRecyclerView.layoutManager = GridLayoutManager(this,2)
        animRecyclerView.setHasFixedSize(true)
        animArrayList = arrayListOf<AnimItem>()
        getDataAnim()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        adContainerView = findViewById(R.id.ad_view_container)
        adView = AdView(this)
        adContainerView.addView(adView)
        adContainerView.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

    }

    private fun getDataAnim() {
        dbref = FirebaseDatabase.getInstance().getReference("anim")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (matchSnapshot in snapshot.children) {
                        val animItem = matchSnapshot.getValue(animItem)
                        animArrayList.add(animItem!!)
                    }
                    animRecyclerView.adapter = AnimAdapterSearch(animArrayList, this@AnimActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun filterList(newText: String?) {
        if (newText != null) {
            val mAnimItem = ArrayList<AnimItem>()
            for (i in animArrayList) {
                if (i.name!!.lowercase(Locale.ROOT).contains(newText)) {
                    mAnimItem.add(i)
                }
            }
            if (mAnimItem.isEmpty()) {
                Toast.makeText(this, "لا يوجد معلومات", Toast.LENGTH_SHORT).show()
            } else {
                animRecyclerView.adapter = AnimAdapterSearch(mAnimItem, this@AnimActivity)
            }

        }
    }
    private fun loadBanner() {
        adView.adUnitId = AD_UNIT_ID

        adView.adSize =adSize

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    companion object {
        // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
        private const val AD_UNIT_ID = "ca-app-pub-3145576516793733/7505224341"
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

}