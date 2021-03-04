package com.devanant.bee.UI.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.devanant.bee.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {


    private ChipNavigationBar bottomNavigation;
    private homeFragment homeFrag;
    private profileFragment profileFrag;
    private messageFragment msgFrag;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFrag=new homeFragment();
        profileFrag=new profileFragment();
        msgFrag=new messageFragment();
        bottomNavigation=findViewById(R.id.bottom_navigation_bar);

        fragmentManager=getSupportFragmentManager();

        if(savedInstanceState==null){
            bottomNavigation.setItemSelected(R.id.nav_home, true);
            replaceFragment(homeFrag);
        }

        bottomNavigation=findViewById(R.id.bottom_navigation_bar);
        bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.nav_msg:
                        replaceFragment(msgFrag);
                        break;
                    case R.id.nav_profile:
                        replaceFragment(profileFrag);
                        break;
                    default:
                        replaceFragment(homeFrag);
                        break;
                }
            }
        });

    }

    private void replaceFragment(Fragment Frag) {
        fragmentManager.beginTransaction().replace(R.id.FragmentContainer,Frag).commit();
    }
}