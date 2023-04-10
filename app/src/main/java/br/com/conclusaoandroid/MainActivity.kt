package br.com.conclusaoandroid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingAdapter
import br.com.conclusaoandroid.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var auth: FirebaseAuth;
    private lateinit var shoppingAdapter: ShoppingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        window.statusBarColor = Color.parseColor("#0075FF")

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            gotoLogin()
        }

        binding.logMenu.logout.setOnClickListener {
            auth.signOut()
            gotoLogin()
        }

        var shoppingQuery = Firebase.firestore
            .collection("shopping")
            .whereEqualTo("userId", auth.uid)
            .limit(50)

        shoppingAdapter = object : ShoppingAdapter(shoppingQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    println("vazio")
                } else {
                    println("so vai")
                }
            }
        }

        binding.recyclerShopping.adapter = shoppingAdapter
    }

    private fun setupListener() {

    }

    private fun gotoLogin() {
        val intent = Intent(this, Login::class.java);
        startActivity(intent);
        finish();
    }

    public override fun onStart() {
        super.onStart()
        shoppingAdapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        shoppingAdapter.stopListening()
    }
}