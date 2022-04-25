package com.example.cloudreveapp.ui.sync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.cloudreveapp.R;
import com.example.cloudreveapp.common.Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTypeActivity extends AppCompatActivity {
//
//    private Button addFileType;
//    private ListView listViewFileType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_activity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.settings, new SettingsFragment())
//                    .commit();
//        }
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//
//        String[] data =getFileTypeListData();
//        listViewFileType = (ListView) findViewById(R.id.listViewFileType);
//
//        //创建一个list集合，list集合的元素是Map
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
//        //为ListView设置Adapter
//        listViewFileType.setAdapter( adapter);
//
//
//        addFileType = (Button) findViewById(R.id.addFileType);
//        addFileType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // add type
//                SharedPreferences sp = getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
//                Set<String> hs = sp.getStringSet(Common.SYNC_FILE_TYPE, new HashSet<String>());
//                SharedPreferences.Editor ed = sp.edit();
//
//                Set<String> hsNew = new HashSet<>();
//                for(String x :hs) {
//                    if(!x.equals(path)) hsNew.add(x);
//                }
//                ed.putStringSet(Common.SYNC_PATHS, hsNew);
//                ed.apply();
//
//
//            }
//        });
//
//
//    }
//
//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        }
//    }
//
//    String[] getFileTypeListData() {
//        List<String> data = new ArrayList<String>();
//
//        SharedPreferences sp = getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
//        Set<String> hs = sp.getStringSet(Common.SYNC_FILE_TYPE, new HashSet<String>());
//        if (!hs.isEmpty()) {
//            for (String s : hs) {
//                data.add(s);
//            }
//        }
//        if (data.size() == 0) {
//
//            Set<String> hw = new HashSet<>();
//
//            for (String x : Common.defaultFileTypes) {
//                data.add("");
//                data.add(x);
//
//                hw.add("");
//                hw.add(x);
//            }
//            SharedPreferences.Editor ed = sp.edit();
//
//            ed.putStringSet(Common.SYNC_FILE_TYPE, hw);
//            ed.apply();
//        }
//
//        String[] res = new String[data.size()];
//
//        return data.toArray(res);
//    }
}