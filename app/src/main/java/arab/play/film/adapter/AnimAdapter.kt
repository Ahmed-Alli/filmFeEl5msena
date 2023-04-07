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


class AnimAdapter(
    private val postList: ArrayList<AnimItem>,
    private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        private const val CONTENT_VIEW = 0
        private const val AD_VIEW = 2

    }

    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img1:ImageView =itemView.findViewById(R.id.img1)
        var name:TextView =itemView.findViewById(R.id.name)
        var episodes:TextView =itemView.findViewById(R.id.episodes)
        var cardItem :CardView =itemView.findViewById(R.id.cardItem)
    }

    inner class AdsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ad_app_icon :MediaView= itemView.findViewById(R.id.ad_app_icon)
        var icon :ImageView= itemView.findViewById(R.id.icon)
        val ad_headline: TextView = itemView.findViewById(R.id.ad_headline)
        val ad_advertiser: TextView = itemView.findViewById(R.id.ad_advertiser)
        val ad_stars: RatingBar = itemView.findViewById(R.id.ad_stars)
        val ad_body: TextView = itemView.findViewById(R.id.ad_body)
        val ad_price: TextView = itemView.findViewById(R.id.ad_price)
        val ad_store: TextView = itemView.findViewById(R.id.ad_store)
        val ad_call_to_action: Button = itemView.findViewById(R.id.ad_call_to_action)
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.ad_view)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        postList.shuffle()
        val view: View
        if (viewType == CONTENT_VIEW) {

            view = LayoutInflater.from(context).inflate(R.layout.anim_card, parent, false)

            return ContentHolder(view)

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.ads, parent, false)

            return AdsHolder(view)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == CONTENT_VIEW) {
            val currentItem = postList[position]
            val contentHolder = holder as ContentHolder
            contentHolder.name.text = currentItem.name
            contentHolder.episodes.text = currentItem.episodes

            contentHolder.cardItem.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_view_background_3)

            try {
                Picasso.get().load(currentItem.img1).placeholder(R.drawable.ic_image).into(contentHolder.img1)
            } catch (e: Exception) {
                contentHolder.img1.setImageResource(R.drawable.ic_image)
            }

            contentHolder.img1.setOnClickListener {

                // on below line we are creating a new bottom sheet dialog.
                val dialog = BottomSheetDialog(context)

                // on below line we are inflating a layout file which we have created.
                val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog,null)
                // on below line we are creating a variable for our button
                // which we are using to dismiss our dialog.
                val btnClose = view.findViewById<ImageView>(R.id.idBtnDismiss)
                val btnplay =view.findViewById<Button>(R.id.btnPlay)
                val img1 = view.findViewById<ImageView>(R.id.img1)
                val img2 = view.findViewById<ImageView>(R.id.img2)
                val img3 = view.findViewById<ImageView>(R.id.img3)
                val img4 = view.findViewById<ImageView>(R.id.img4)
                val name =view.findViewById<TextView>(R.id.name)
                val rate =view.findViewById<TextView>(R.id.rate)
                val story =view.findViewById<TextView>(R.id.story)
                val category =view.findViewById<TextView>(R.id.category)

                try {
                    Picasso.get().load(currentItem.img1).placeholder(R.drawable.ic_image).into(img1)
                    Picasso.get().load(currentItem.img2).placeholder(R.drawable.ic_image).into(img2)
                    Picasso.get().load(currentItem.img3).placeholder(R.drawable.ic_image).into(img3)
                    Picasso.get().load(currentItem.img4).placeholder(R.drawable.ic_image).into(img4)
                } catch (e: Exception) {
                    img1.setImageResource(R.drawable.ic_image)
                    img2.setImageResource(R.drawable.ic_image)
                    img3.setImageResource(R.drawable.ic_image)
                    img4.setImageResource(R.drawable.ic_image)
                }

                name.text  = currentItem.name
                rate.text = currentItem.rate
                story.text = currentItem.story
                category.text =currentItem.category
                // on below line we are adding on click listener
                // for our dismissing the dialog button.
                btnClose.setOnClickListener {
                    // on below line we are calling a dismiss
                    // method to close our dialog.
                    dialog.dismiss()
                }

                btnplay.setOnClickListener {
                    val intent = Intent(context, YouTubeActivity::class.java)
                    intent.putExtra("name",currentItem.name)
                    intent.putExtra("category",currentItem.category)
                    intent.putExtra("rate",currentItem.rate)
                    intent.putExtra("story",currentItem.story)
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




        } else if (getItemViewType(position) == AD_VIEW) {

            val adLoader = AdLoader.Builder(
                context, "ca-app-pub-3145576516793733/7098934190"
            ).forNativeAd { nativeAd ->
                val adsHolder = holder as AdsHolder
                displayNativeAd(adsHolder, nativeAd)

            }.withAdListener(object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }

            }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            adLoader.loadAd(AdRequest.Builder().build())

        }



    }

    private fun displayNativeAd(holderNativeAd: AdsHolder, nativeAd: NativeAd) {
        val headline = nativeAd.headline
        val body = nativeAd.body
        val icon = nativeAd.icon
        val advertiser = nativeAd.advertiser
        val price = nativeAd.price
        val starRating = nativeAd.starRating
        val callToAction = nativeAd.callToAction
        val store = nativeAd.store
        val media = nativeAd.mediaContent



        if (headline == null) {
            holderNativeAd.ad_headline.visibility = View.GONE
        } else {
            holderNativeAd.ad_headline.visibility = View.VISIBLE
            holderNativeAd.ad_headline.text = headline
        }

        if (body == null) {
            holderNativeAd.ad_body.visibility = View.GONE
        } else {
            holderNativeAd.ad_body.visibility = View.VISIBLE
            holderNativeAd.ad_body.text = body
        }

        if (advertiser == null) {
            holderNativeAd.ad_advertiser.visibility = View.GONE
        } else {
            holderNativeAd.ad_advertiser.visibility = View.VISIBLE
            holderNativeAd.ad_advertiser.text = advertiser
        }

        if (price == null) {
            holderNativeAd.ad_price.visibility = View.GONE
        } else {
            holderNativeAd.ad_price.visibility = View.VISIBLE
            holderNativeAd.ad_price.text = price
        }

        if (store == null) {
            holderNativeAd.ad_store.visibility = View.GONE
        } else {
            holderNativeAd.ad_store.visibility = View.VISIBLE
            holderNativeAd.ad_store.text = store
        }

        if (icon == null) {
            holderNativeAd.icon.visibility = View.GONE
        } else {
            holderNativeAd.icon.visibility = View.VISIBLE
            holderNativeAd.icon.setImageDrawable(icon.drawable)
        }



        if (starRating == null) {
            holderNativeAd.ad_stars.visibility = View.GONE
        } else {
            holderNativeAd.ad_stars.visibility = View.VISIBLE
            holderNativeAd.ad_stars.rating = starRating.toFloat()
        }

        if (callToAction == null) {
            holderNativeAd.ad_call_to_action.visibility = View.GONE
        } else {
            holderNativeAd.ad_call_to_action.visibility = View.VISIBLE
            holderNativeAd.ad_call_to_action.text = callToAction
            holderNativeAd.nativeAdView.callToActionView = holderNativeAd.ad_call_to_action

        }

        if (media == null) {
            holderNativeAd.ad_app_icon.visibility = View.GONE
        } else {
            holderNativeAd.nativeAdView.mediaView = holderNativeAd.ad_app_icon
            holderNativeAd.nativeAdView.setNativeAd(nativeAd)
            holderNativeAd.ad_app_icon.setMediaContent(media)
        }
        holderNativeAd.nativeAdView.setNativeAd(nativeAd)
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position%5 == 2){
            AD_VIEW
        }else{
            CONTENT_VIEW
        }
    }
}