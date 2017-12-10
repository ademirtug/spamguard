package net.demirtug.akin.spamguard;


import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    public static boolean activated = true;
    ArrayAdapter<String> adapter;
    SharedPreferences sp;
    SharedPreferences.Editor se;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle(R.string.title_home);
                    change_page(new AnaSayfa());

                    return true;
                case R.id.navigation_keywords:
                    setTitle(R.string.title_keywords);
                    change_page(new SpamListSayfa());

                    return true;
                case R.id.navigation_trash:
                    setTitle(R.string.title_trash);
                    change_page(new CopSayfa());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        change_page(new AnaSayfa());
    }

    protected void change_page(Fragment fr)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.inner_frame, fr);
        ft.commit();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void btn_shield_click(View v)
    {
        ImageButton btn_shield = v.findViewById(R.id.btn_shield);

        MainActivity.activated = !MainActivity.activated;
        btn_shield.setAlpha(MainActivity.activated ? 1.0f : .3f);

    }
}
