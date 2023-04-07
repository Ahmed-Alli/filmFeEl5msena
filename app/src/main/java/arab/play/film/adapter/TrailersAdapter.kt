package arab.play.film.adapter

import arab.play.film.YouTubeActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.R
import arab.play.film.apiActivity.AnimItem
import arab.play.film.apiActivity.Trailers
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso


class TrailersAdapter(
    private val postList: ArrayList<Trailers>,
    private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img1:ImageView =itemView.findViewById(R.id.trailerImg)
        var name:TextView =itemView.findViewById(R.id.trailerText)
        var btn:ImageView = itemView.findViewById(R.id.trailerBtn)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        postList.shuffle()
        val view = LayoutInflater.from(context).inflate(R.layout.card_trailer, parent, false)
            return ContentHolder(view)

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val currentItem = postList[position]
            val contentHolder = holder as ContentHolder
            contentHolder.name.text = currentItem.trailerText

            try {
                Picasso.get().load(currentItem.trailerImg).placeholder(R.drawable.ic_image).into(contentHolder.img1)
            } catch (e: Exception) {
                contentHolder.img1.setImageResource(R.drawable.ic_image)
            }


            contentHolder.btn.setOnClickListener {

                // on below line we are creating a new bottom sheet dialog.
                val dialog = BottomSheetDialog(context)

                // on below line we are inflating a layout file which we have created.
                val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog,null)
                // on below line we are creating a variable for our button
                // which we are using to dismiss our dialog.
                val btnClose = view.findViewById<ImageView>(R.id.idBtnDismiss)
                val btnplay =view.findViewById<Button>(R.id.btnPlay)
                val img1 = view.findViewById<ImageView>(R.id.img1)
                val name =view.findViewById<TextView>(R.id.name)


                try {
                    Picasso.get().load(currentItem.trailerImg).placeholder(R.drawable.ic_image).into(img1)
                } catch (e: Exception) {
                    img1.setImageResource(R.drawable.ic_image)
                }

                name.text  = currentItem.trailerText
                // on below line we are adding on click listener
                // for our dismissing the dialog button.
                btnClose.setOnClickListener {
                    // on below line we are calling a dismiss
                    // method to close our dialog.
                    dialog.dismiss()
                }

                btnplay.setOnClickListener {
                    val intent = Intent(context, YouTubeActivity::class.java)
                    intent.putExtra("name",currentItem.trailerText)
                    intent.putExtra("videoId",currentItem.videoId)
                    context.startActivity(intent)
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
            }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}