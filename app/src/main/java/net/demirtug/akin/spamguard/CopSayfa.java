package net.demirtug.akin.spamguard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CopSayfa extends Fragment {
    SimpleAdapter adapter;
    SharedPreferences sp;
    SharedPreferences.Editor se;
    ListView lst_spammessages;

    public CopSayfa() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_cop_sayfa, container, false);
        lst_spammessages = v.findViewById(R.id.lst_spammessages);
        setupview();

        lst_spammessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

                ArrayList<String> spamarray = new  ArrayList<String>();
                sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                String messages = sp.getString("net.demirtug.akin.spamguard.spammessages", null);


                if( messages!= null && messages.length() > 0)
                    spamarray = new  ArrayList<String>( Arrays.asList(messages.split(";")) );

                for(int i = 0; i < spamarray.size(); ++i )
                {
                    String[] msg = spamarray.get(i).replaceAll(";", "").split("#");
                    if(msg.length < 2)
                        continue;

                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("telefon", msg[0]);
                    datum.put("mesaj",msg[1].length() > 40 ? msg[1].substring(0, 39) + "..." : msg[1]);
                    data.add(datum);
                }

                builder.setMessage(data.get(position).get("mesaj").toString());
                builder.setTitle(data.get(position).get("telefon").toString());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        registerForContextMenu(lst_spammessages);
        return v;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(((HashMap<String, String>)adapter.getItem(info.position)).get("telefon").toString());
        menu.add(Menu.NONE, 0, 0, "Sil");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int cmd = item.getOrder();

        if (cmd == 0) {
            String messages = sp.getString("net.demirtug.akin.spamguard.spammessages", null);
            ArrayList<String> spamarray;

            if (messages == null && messages.length() < 1)
                return false;

            HashMap<String, String> dx = (HashMap<String, String>)adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);

            String rx = dx.get("telefon").toString() + "#" + dx.get("mesaj").toString() + "([;]*)";
            rx = rx.replace("+", "\\+");
            messages = messages.replaceAll(rx, "");
            se = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            se.putString("net.demirtug.akin.spamguard.spammessages", messages);
            se.commit();

            setupview();
        }
        return true;
    }

    void setupview()
    {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        ArrayList<String> spamarray = new  ArrayList<String>();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String messages = sp.getString("net.demirtug.akin.spamguard.spammessages", null);


        if( messages!= null && messages.length() > 0)
            spamarray = new  ArrayList<String>( Arrays.asList(messages.split(";")) );

        for(int i = 0; i < spamarray.size(); ++i )
        {
            String[] msg = spamarray.get(i).replaceAll(";", "").split("#");
            if(msg.length < 2)
                continue;

            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("telefon", msg[0]);
            datum.put("mesaj",msg[1].length() > 40 ? msg[1].substring(0, 39) + "..." : msg[1]);
            data.add(datum);
        }

        adapter = new SimpleAdapter(getContext(), data,
                android.R.layout.simple_list_item_2,
                new String[] {"telefon", "mesaj" },
                new int[] {android.R.id.text1, android.R.id.text2 });


        lst_spammessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
