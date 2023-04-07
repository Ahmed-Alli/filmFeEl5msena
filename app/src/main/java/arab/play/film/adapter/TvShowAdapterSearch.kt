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
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import arab.play.film.R
import arab.play.film.apiActivity.TvShowItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso


class TvShowAdapterSearch(
    private val postList: ArrayList<TvShowItem>,
    private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img1:ImageView =itemView.findViewById(R.id.img1)
        var name:TextView =itemView.findViewById(R.id.name)
        var episodes:TextView =itemView.findViewById(R.id.episodes)
        var cardItem :CardView =itemView.findViewById(R.id.cardItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.anim_card, parent, false)
            return ContentHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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

    }

    override fun getItemCount(): Int {
        return postList.size
    }

}