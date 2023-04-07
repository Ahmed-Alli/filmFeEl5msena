package arab.play.film

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        val t1ext =findViewById<TextView>(R.id.t1ext)
        val t2ext =findViewById<TextView>(R.id.t2ext)
        val t3ext =findViewById<TextView>(R.id.t3ext)
        val textView8 =findViewById<TextView>(R.id.textView8)

        t1ext.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://adssettings.google.ae/anonymous?hl=en")
            startActivity(intent)
        }
        t2ext.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://support.google.com/admanager/answer/1670087?visit_id=638092161344889474-656945927&rd=1")
            startActivity(intent)
        }
        t3ext.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://support.google.com/admanager/answer/94149")
            startActivity(intent)
        }
        textView8.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://sport.arab-play.com/p/blog-page_13.html")
            startActivity(intent)
        }
    }
}