package com.example.bookapplication.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookapplication.R

lateinit var linkedin:TextView
lateinit var github:TextView
lateinit var instagram : TextView
class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        linkedin = view.findViewById(R.id.linkedin)
        linkedin.setMovementMethod(LinkMovementMethod.getInstance())
        github = view.findViewById(R.id.github)
        github.setMovementMethod(LinkMovementMethod.getInstance())
        instagram = view.findViewById(R.id.instagram)
        instagram.setMovementMethod(LinkMovementMethod.getInstance())
        return view
    }
}