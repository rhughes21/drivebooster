package com.personal.drivebooster

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.personal.drivebooster.Adapter.CustomPupilsAdapter
import java.util.*

class MyPupilsFragment : Fragment() {


    val usersFromFirebase: List<Users> = ArrayList()
    var customPupilAdapter: CustomPupilsAdapter? = null
    var databaseBookingRef:DatabaseReference? = null
    var pupilRecycler: RecyclerView? = null
    var myName: String? = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater!!.inflate(R.layout.instructors_my_pupils_fragment, container, false)
        pupilRecycler = view?.findViewById(R.id.my_pupils_recycler)


        val linearLayoutManager = LinearLayoutManager(context)
        pupilRecycler?.layoutManager = linearLayoutManager
        customPupilAdapter = CustomPupilsAdapter(usersFromFirebase)
        pupilRecycler?.adapter = customPupilAdapter
        getMyName()
        getMyPupils()
        return view
    }

    private fun getMyPupils() {
        databaseBookingRef = FirebaseDatabase.getInstance().reference.child("Users")
        databaseBookingRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    val children = snapshot.children
                    for (child in children) {
                        val users = child.getValue(Users::class.java)!!
                        if (users.instructorName == myName) {
                            usersFromFirebase.toMutableSet().add(users)
                            Log.i("users list", users.name.toString())
                            customPupilAdapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getMyName(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser!!.uid

        databaseBookingRef = FirebaseDatabase.getInstance().reference.child("Instructors").child(userId)
        databaseBookingRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                myName = snapshot.child("name").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}