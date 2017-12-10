package net.demirtug.akin.spamguard;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpamListSayfa extends Fragment {

    ArrayAdapter<String> adapter;
    SharedPreferences sp;
    SharedPreferences.Editor se;
    EditText txt_keyword;
    ListView lst_keywords;

    public SpamListSayfa() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_spam_list_sayfa, container, false);

        lst_keywords = (ListView) v.findViewById(R.id.lst_keywords);
        ArrayList<String> spamarray = new  ArrayList<String>();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String keywords = sp.getString("net.demirtug.akin.spamguard.keywords", null);


        if( keywords!= null && keywords.length() > 0)
            spamarray = new  ArrayList<String>( Arrays.asList(keywords.split(";")) );


        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, spamarray);
        lst_keywords.setAdapter(adapter);


        Button btn_addkeyword = (Button) v.findViewById(R.id.btn_addkeyword);
        txt_keyword = (EditText)v.findViewById(R.id.txt_keyword);

        btn_addkeyword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View vx)
            {
                String input = txt_keyword.getText().toString();
                if( input.length() > 0 ) {
                    if( adapter.getPosition(input) < 0 ) {
                        adapter.add(input.toString());
                        txt_keyword.setText("");

                        ArrayList<String> tl = new ArrayList<String>();

                        for(int i = 0;i < adapter.getCount(); ++i)
                            tl.add(adapter.getItem(i).toString());

                        se = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        se.putString("net.demirtug.akin.spamguard.keywords", TextUtils.join(";", tl));
                        se.commit();
                    }
                    else {
                        Toast.makeText(getContext(), "zaten var", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "birşey yazın", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lst_keywords = v.findViewById(R.id.lst_keywords);
        registerForContextMenu(lst_keywords);
        return v;

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(adapter.getItem(info.position));
        menu.add(Menu.NONE, 0, 0, "Sil");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int cmd = item.getOrder();

        if( cmd == 0)
        {
            String stored  = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("net.demirtug.akin.spamguard.keywords", null);
            stored = stored.replaceAll(adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position) + "([;]*)", "");

            se = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            se.putString("net.demirtug.akin.spamguard.keywords", stored);
            se.commit();

            adapter.remove(adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position));

        }
        return true;
    }
}
