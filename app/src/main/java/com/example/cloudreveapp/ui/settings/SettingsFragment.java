package com.example.cloudreveapp.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudreveapp.R;
import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.databinding.FragmentSettingsBinding;
import com.example.cloudreveapp.task.fileSyncTask;

import java.util.HashSet;
import java.util.Set;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Button startSyncFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        startSyncFile = (Button) root.findViewById(R.id.startSyncFile);
        startSyncFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String [] syncPaths=getSyncPaths();
                String msg ="";
                for(String x:syncPaths){
                    msg+=x+"\n";
                }

                Toast.makeText(getActivity(), "开始同步文件夹 "+msg,
                        Toast.LENGTH_SHORT).show();
                fileSyncTask fst = new fileSyncTask(syncPaths, Common.DefaultNotSyncPaths, Common.defaultFileTypes,
                        Common.UserHostURL, Common.loginCookie);
                fst.start();

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    String[] getSyncPaths() {

        SharedPreferences sp = getActivity().getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
        Set<String> hs = sp.getStringSet(Common.SYNC_PATHS, new HashSet<String>());

        String[] res = new String[hs.size()];
        res = hs.toArray(res);

        return res;
    }
}