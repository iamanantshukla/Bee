package com.devanant.bee.Firebase;

import android.app.Application;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthentication {

    private FirebaseAuth mAuth;
    private Application application;
    private View SnackView;

    public FirebaseAuthentication(FirebaseAuth mAuth, Application application, View SnackView) {
        this.mAuth = mAuth;
        this.application = application;
        this.SnackView=SnackView;
    }


}
