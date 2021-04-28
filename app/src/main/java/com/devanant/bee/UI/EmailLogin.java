package com.devanant.bee.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devanant.bee.Database.TinyDB;
import com.devanant.bee.R;
import com.devanant.bee.UI.Home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailLogin extends AppCompatActivity {

    private int STATUS=0;
    private Button Next, GoogleSignUp;
    private TextView EditEmail, EditPassword, Header, footer;
    private String Email,Password;
    private FirebaseAuth mAuth;
    private View parentLayout;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout emailLayout, phoneLayout;
    private int RC_SIGN_IN=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emal_login);

        //transition Time period
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(800);
            getWindow().getSharedElementReturnTransition().setDuration(800)
                    .setInterpolator(new DecelerateInterpolator());
        }

        parentLayout=findViewById(android.R.id.content);

        //getting Status 0->new User, 1->Old user login
        STATUS=getIntent().getIntExtra("Status",0);

        Next=findViewById(R.id.btnNext);
        GoogleSignUp=findViewById(R.id.btnGoogle);

        EditEmail=findViewById(R.id.EditEmail);
        EditPassword=findViewById(R.id.EditPassword);

        Header=findViewById(R.id.TextHeader);
        footer=findViewById(R.id.textStatus);
        emailLayout=findViewById(R.id.linearEmail);
        phoneLayout=findViewById(R.id.linearPhone);


        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(STATUS==0){
                    STATUS=1;
                }else{
                    STATUS=0;
                }
                changeUI();
            }
        });

        mAuth=FirebaseAuth.getInstance();

        changeUI();

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email=EditEmail.getText().toString();
                Password=EditPassword.getText().toString();
                if(check(Email,Password)){
                    if(STATUS==0){
                        mAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Verification link has been sent on your email", Toast.LENGTH_SHORT).show();
                                        EditEmail.setText("");
                                        EditPassword.setText("");
                                        STATUS=1;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed to sent email verification
                                        Toast.makeText(getApplicationContext(),"Failed to send verification link", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed to create
                                Toast.makeText(getApplicationContext(),"Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Email=EditEmail.getText().toString();
                        Password=EditPassword.getText().toString();
                        if(check(Email,Password)){
                            mAuth.signInWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {


                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Map<String, Object> map=new HashMap<>();

                                                    map=snapshot.getData();
                                                    TinyDB tinyDB=new TinyDB(getApplicationContext());
                                                    tinyDB.putObject("UserProfile", map);

                                                    Intent i=new Intent(EmailLogin.this, HomeActivity.class);
                                                    startActivity(i);
                                                }else{

                                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(EmailLogin.this, PhoneLogin.class);
                                                    i.putExtra("Method",0);
                                                    i.putExtra("email",Email);
                                                    i.putExtra("password", Password);
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(EmailLogin.this,
                                                            new Pair<View, String>(emailLayout, "emailTransition"),
                                                            new Pair<View, String>(phoneLayout, "phoneTransition"));
                                                    startActivity(i, options.toBundle());
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Verify your email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //google sign up
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.i("GoogleFail", "onActivityResult: "+e.getMessage());
                // Google Sign In failed, update UI appropriately
                Snackbar.make(parentLayout, "Google Sign in Failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        Map<String, Object> map=new HashMap<>();

                                        map=snapshot.getData();
                                        TinyDB tinyDB=new TinyDB(getApplicationContext());
                                        tinyDB.putObject("UserProfile", map);

                                        Intent i=new Intent(EmailLogin.this, HomeActivity.class);
                                        startActivity(i);
                                    }else{
                                        Snackbar.make(parentLayout,"Login Successful",Snackbar.LENGTH_SHORT);
                                        Intent i=new Intent(EmailLogin.this, PhoneLogin.class);
                                        i.putExtra("Method",1);
                                        i.putExtra("GoogleIDToken",idToken);
                                        startActivity(i);
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(parentLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeUI() {
        if(STATUS==0){
            footer.setText(R.string.AlreadyAccount);
            Header.setText(R.string.HeaderSignUp);
        }else{
            footer.setText(R.string.NewAccount);
            Header.setText(R.string.HeaderLogin);
        }
    }

    private boolean check(String email, String password) {
        if(email.isEmpty()){
            EditEmail.setError("Email is empty");
            return false;
        }else if(password.isEmpty()){
            EditPassword.setError("Password is empty");
            return  false;
        }else {
            if (password.length() < 6) {
                EditPassword.setError("Password is too short");
                return false;
            } else {
                return true;
            }
        }
    }
}