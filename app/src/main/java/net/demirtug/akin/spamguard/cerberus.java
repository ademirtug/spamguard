package net.demirtug.akin.spamguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;

public class cerberus extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if( !MainActivity.activated )
            return;

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");

                if (pdus.length < 1)
                    return;

                String stored = PreferenceManager.getDefaultSharedPreferences(context).getString("net.demirtug.akin.spamguard.keywords", null);
                if(stored == null || stored.length() < 1)
                    return;

                String[] keywords = stored.split(";");


                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);


                    String sender = message.getOriginatingAddress();

                    if( inContacts(context, sender))
                        continue;

                    String text = message.getMessageBody();

                    text = text.toLowerCase();

                    for(int x = 0; x < keywords.length;++x){
                        String kw = keywords[x].toLowerCase(context.getResources().getConfiguration().locale);
                        if( text.contains( kw ) ) {
                            abortBroadcast();

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                            String messages = sp.getString("net.demirtug.akin.spamguard.spammessages", null);
                            ArrayList<String> tl = new ArrayList<String>();


                            if( messages != null && messages.length() > 0)
                            {
                                ArrayList<String> msgarray = new ArrayList<String>(Arrays.asList(messages.split(";")));
                                for(int j = 0; j < msgarray.size();++j)
                                    tl.add(msgarray.get(j));
                            }

                            text = text.replaceAll(";", "").replaceAll("#","");
                            tl.add(sender + "#"+ text);

                            SharedPreferences.Editor se = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            se.putString("net.demirtug.akin.spamguard.spammessages", TextUtils.join(";", tl));
                            se.commit();

                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    public boolean inContacts(Context context, String pn) {

        boolean f = false;
        Cursor contacts = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (contacts.moveToNext())
        {

            String cn = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if( pn.contains(cn) ){
                f = true;
                break;
            }
        }
        contacts.close();

        return f;
    }
}