package com.devanant.bee.Firebase;

import android.app.Application;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirebaseUser {

    private Application application;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    public FirebaseUser(Application application) {
        this.application = application;
    }


}
