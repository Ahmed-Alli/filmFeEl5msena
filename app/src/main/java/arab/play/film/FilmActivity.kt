package arab.play.film

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.adapter.FilmAdapter
import arab.play.film.adapter.FilmAdapterSearch
import arab.play.film.apiActivity.FilmItem
import arab.play.film.databinding.ActivityFilmBinding
import arab.play.film.databinding.ActivityYoutubeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.database.*
import java.util.*

class FilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilmBinding
    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false
    private val adSize: AdSize
        get() {
            return AdSize.BANNER
        }
    private val filmItem = FilmItem::class.java
    private lateinit var dbref: DatabaseReference
    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var filmArrayList: ArrayList<FilmItem>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filter()
        searchView = findViewById(R.id.searchView)
        filmRecyclerView = findViewById(R.id.rv)
        filmRecyclerView.layoutManager = GridLayoutManager(this,2)
        filmRecyclerView.setHasFixedSize(true)
        filmArrayList = arrayListOf<FilmItem>()
        getDataFilms()

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

    private fun filter(){
        binding.action.setOnClickListener {
            filterListCategory("اكشن")
        }
        binding.drama.setOnClickListener {
            filterListCategory("دراما")
        }
        binding.visal.setOnClickListener {
            filterListCategory("خيال علمي")
        }
        binding.horror.setOnClickListener {
            filterListCategory("رعب")
        }
        binding.cirm.setOnClickListener {
            filterListCategory("جريمة")
        }
        binding.main.setOnClickListener {
            getDataFilms()
        }
        binding.Animation.setOnClickListener {
            filterListCategory("كارتون")
        }
        binding.Biography.setOnClickListener {
            filterListCategory("السيره الذاتيه")
        }
        binding.Documentary.setOnClickListener {
            filterListCategory("وثائقي")
        }
        binding.Romance.setOnClickListener {
            filterListCategory("رومانسي")
        }
        binding.War.setOnClickListener {
            filterListCategory("حرب")
        }
        binding.Comedy.setOnClickListener {
            filterListCategory("كوميدي")
        }
        binding.Hirstory.setOnClickListener {
            filterListCategory("تاريخي")
        }
    }


    private fun filterListCategory(newText: String?) {
        if (newText != null) {
            val mFilmItem = ArrayList<FilmItem>()
            for (i in filmArrayList) {
                if (i.category!!.lowercase(Locale.ROOT).contains(newText)) {
                    mFilmItem.add(i)
                }
            }
            if (mFilmItem.isEmpty()) {
                Toast.makeText(this, "لا يوجد معلومات", Toast.LENGTH_SHORT).show()
            } else {
                filmRecyclerView.adapter = FilmAdapterSearch(mFilmItem, this@FilmActivity)
            }

        }
    }
    private fun filterList(newText: String?) {
        if (newText != null) {
            val mFilmItem = ArrayList<FilmItem>()
            for (i in filmArrayList) {
                if (i.name!!.lowercase(Locale.ROOT).contains(newText)) {
                    mFilmItem.add(i)
                }
            }
            if (mFilmItem.isEmpty()) {
                Toast.makeText(this, "لا يوجد معلومات", Toast.LENGTH_SHORT).show()
            } else {
                filmRecyclerView.adapter = FilmAdapterSearch(mFilmItem, this@FilmActivity)
            }

        }
    }


    private fun getDataFilms() {
        dbref = FirebaseDatabase.getInstance().getReference("films")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (matchSnapshot in snapshot.children) {
                        val filmItem = matchSnapshot.getValue(filmItem)
                        filmArrayList.add(filmItem!!)
                    }

                    filmRecyclerView.adapter = FilmAdapterSearch(filmArrayList, this@FilmActivity)

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
        private const val AD_UNIT_ID = "ca-app-pub-3145576516793733/1649691610"
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