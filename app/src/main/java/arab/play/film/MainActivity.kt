package arab.play.film

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.adapter.AnimAdapter
import arab.play.film.adapter.FilmAdapter
import arab.play.film.adapter.TrailersAdapter
import arab.play.film.adapter.TvShowAdapter
import arab.play.film.apiActivity.*
import arab.play.film.databinding.ActivityMainBinding
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import io.github.kobakei.materialfabspeeddial.FabSpeedDial
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    private lateinit var adView: AdView
    private lateinit var adContainerView: FrameLayout
    private var initialLayoutComplete = false
    private val adSize: AdSize
        get() {
            return AdSize.BANNER
        }

    private val filmItem = FilmItem::class.java
    private val tvShowItem = TvShowItem::class.java
    private val animItem = AnimItem::class.java
    private val slideShowItem = SlideShowItem::class.java
    private val trailers = Trailers::class.java

    private lateinit var dbref: DatabaseReference

    private lateinit var filmRecyclerView: RecyclerView

    private lateinit var tvShowRecyclerView: RecyclerView

    private lateinit var animRecyclerView: RecyclerView
    private lateinit var trailerRc : RecyclerView

    private lateinit var filmArrayList: ArrayList<FilmItem>
    private lateinit var tvShowArrayList: ArrayList<TvShowItem>
    private lateinit var animArrayList: ArrayList<AnimItem>
    private lateinit var slideShowArrayList : ArrayList<SlideShowItem>
    private lateinit var trailerList :ArrayList<Trailers>


    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnMore()
        getDataFilms()
        getDataAnim()
        getDataTvShow()
        getDataSlideShow()
        getDataTrailers()
        btn()

        adContainerView = findViewById(R.id.ad_view_container)
        adView = AdView(this)
        adContainerView.addView(adView)
        adContainerView.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }


        filmRecyclerView = findViewById(R.id.rec1)
        tvShowRecyclerView = findViewById(R.id.recMos)
        animRecyclerView = findViewById(R.id.recAnm)
        trailerRc = findViewById(R.id.trailerRc)




        trailerRc.layoutManager=
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        trailerRc.setHasFixedSize(true)
        trailerList = arrayListOf<Trailers>()

        filmRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filmRecyclerView.setHasFixedSize(true)
        filmArrayList = arrayListOf<FilmItem>()


        tvShowRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tvShowRecyclerView.setHasFixedSize(true)
        tvShowArrayList= arrayListOf<TvShowItem>()

        animRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        animRecyclerView.setHasFixedSize(true)
        animArrayList = arrayListOf<AnimItem>()
        slideShowArrayList = arrayListOf<SlideShowItem>()




    }

 private fun btn(){
     binding.film.setOnClickListener {
         val intent = Intent(this,FilmActivity::class.java)
         startActivity(intent)
     }
     binding.tvShow.setOnClickListener {
         val intent = Intent(this,TvShowsActivity::class.java)
         startActivity(intent)
     }
     binding.anim.setOnClickListener {
         val intent = Intent(this,AnimActivity::class.java)
         startActivity(intent)
     }
 }




    private fun btnMore() {
        val btnMore = findViewById<ImageView>(R.id.btnMore)
        btnMore.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            // on below line we are creating a new bottom sheet dialog.
            // on below line we are inflating a layout file which we have created.
            val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_more,null)
            val btnClose = view.findViewById<ImageView>(R.id.idBtnDismiss2)
            val btnRate =  view.findViewById<ImageView>(R.id.imageView8)
           val btnS =  view.findViewById<ImageView>(R.id.btnShare)
           val pb =  view.findViewById<ImageView>(R.id.pb)

            pb.setOnClickListener {
                val intent= Intent(this,PrivacyPolicy::class.java)
                startActivity(intent)
            }
            btnS.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$packageName")
                    type = "text/plain"
                }
                btnRate.setOnClickListener {
                    val uri: Uri = Uri.parse("market://details?id=$packageName")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    try {
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
                    }
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(false)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(view)
            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
            btnClose.setOnClickListener {
                // on below line we are calling a dismiss
                // method to close our dialog.
                dialog.dismiss()
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

                    filmRecyclerView.adapter = FilmAdapter(filmArrayList, this@MainActivity)

                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun getDataSlideShow() {
        dbref = FirebaseDatabase.getInstance().getReference("slideShow")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (matchSnapshot in snapshot.children) {
                        val slideShowItem = matchSnapshot.getValue(slideShowItem)
                        slideShowArrayList.add(slideShowItem!!)
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })


    }


    private fun getDataTrailers() {
        dbref = FirebaseDatabase.getInstance().getReference("Trailers")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (matchSnapshot in snapshot.children) {

                        val trailersItem = matchSnapshot.getValue(trailers)
                        trailerList.add(trailersItem!!)

                    }
                    trailerRc.adapter = TrailersAdapter(trailerList, this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


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
                    tvShowRecyclerView.adapter = TvShowAdapter(tvShowArrayList, this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


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
                    animRecyclerView.adapter = AnimAdapter(animArrayList, this@MainActivity)
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
        private const val AD_UNIT_ID = "ca-app-pub-3145576516793733/1102896707"
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