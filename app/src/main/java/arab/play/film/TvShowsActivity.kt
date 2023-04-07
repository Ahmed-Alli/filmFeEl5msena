package arab.play.film

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.adapter.FilmAdapter
import arab.play.film.adapter.TvShowAdapter
import arab.play.film.adapter.TvShowAdapterSearch
import arab.play.film.apiActivity.FilmItem
import arab.play.film.apiActivity.TvShowItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.database.*
import java.util.*

class TvShowsActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false
    private val adSize: AdSize
        get() {
            return AdSize.BANNER
        }

    private val tvShowItem = TvShowItem::class.java
    private lateinit var tvShowRecyclerView: RecyclerView
    private lateinit var tvShowArrayList: ArrayList<TvShowItem>
    private lateinit var dbref: DatabaseReference
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_shows)


        adContainerView = findViewById(R.id.ad_view_container)
        adView = AdView(this)
        adContainerView.addView(adView)
        adContainerView.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

        tvShowRecyclerView = findViewById(R.id.rv)
        searchView = findViewById(R.id.searchView)

        tvShowRecyclerView.layoutManager = GridLayoutManager(this,2)
        tvShowRecyclerView.setHasFixedSize(true)
        tvShowArrayList= arrayListOf<TvShowItem>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        getDataTvShow()
    }

    private fun filterList(newText: String?) {
        if (newText != null) {
            val mTvShowItem = ArrayList<TvShowItem>()
            for (i in tvShowArrayList) {
                if (i.name!!.lowercase(Locale.ROOT).contains(newText)) {
                    mTvShowItem.add(i)
                }
            }
            if (mTvShowItem.isEmpty()) {
                Toast.makeText(this, "لا يوجد معلومات", Toast.LENGTH_SHORT).show()
            } else {
                tvShowRecyclerView.adapter = TvShowAdapterSearch(mTvShowItem, this@TvShowsActivity)
            }

        }
    }

    private fun getDataTvShow() {
        dbref = FirebaseDatabase.getInstance().getReference("tvShows")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (matchSnapshot in snapshot.children) {

                        val tvShowItem = matchSnapshot.getValue(tvShowItem)
                        tvShowArrayList.add(tvShowItem!!)

                    }
                    tvShowRecyclerView.adapter = TvShowAdapterSearch (tvShowArrayList, this@TvShowsActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


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
        private const val AD_UNIT_ID = "ca-app-pub-3145576516793733/4140694409"
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