package com.example.cloudreveapp.ui.sync;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.cloudreveapp.R;
import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.databinding.FragmentSyncBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import com.leon.lfilepickerlibrary.LFilePicker;


public class SyncFragment extends ListFragment  {

    private FragmentSyncBinding binding;
    private ListView listView;

    @Override
    public void onListItemClick(@NonNull ListView a, @NonNull View v, int position, long id) {

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("是否删除该条？")//设置标题
                .setMessage("确定删除该条吗？")//设置提示内容
                //确定按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = (String) a.getItemAtPosition(position);
                        Log.i("onItemClick", "select path is " + path);

                        SharedPreferences sp = getActivity().getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
                        Set<String> hs = sp.getStringSet(Common.SYNC_PATHS, new HashSet<String>());
                        SharedPreferences.Editor ed = sp.edit();

                        Set<String> hsNew = new HashSet<>();
                        for(String x :hs) {
                            if(!x.equals(path)) hsNew.add(x);
                        }
                        ed.putStringSet(Common.SYNC_PATHS, hsNew);
                        ed.apply();

                    }
                })
                //取消按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();//创建对话框
        dialog.show();//显示对话框
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        refreshList();
        return root;
    }


    public void refreshList() {

        //创建一个list集合，list集合的元素是Map
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getSyncListData());
        //为ListView设置Adapter
        setListAdapter(adapter);
    }

    String[] getSyncListData() {
        List<String> data = new ArrayList<String>();

        SharedPreferences sp = getActivity().getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
        Set<String> hs = sp.getStringSet(Common.SYNC_PATHS, new HashSet<String>());
        if (!hs.isEmpty()) {
            for (String s : hs) {
                data.add(s);
            }
        }
        if (data.size() == 0) {
            data.add("");
            data.add("/storage/emulated/0/DCIM");
            data.add("/storage/emulated/0/Pictures");

            SharedPreferences.Editor ed = sp.edit();

            Set<String> hw = new HashSet<>();
            hw.add("");
            hw.add("/storage/emulated/0/DCIM");
            hw.add("/storage/emulated/0/Pictures");
            ed.putStringSet(Common.SYNC_PATHS, hw);
            ed.apply();
        }

        String[] res = new String[data.size()];

        return data.toArray(res);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}