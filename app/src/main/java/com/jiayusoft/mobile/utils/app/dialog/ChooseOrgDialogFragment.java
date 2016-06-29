package com.jiayusoft.mobile.utils.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

/**
 * Created by ASUS on 2014/12/16.
 */
public class ChooseOrgDialogFragment extends DialogFragment {

    public static ChooseOrgDialogFragment newInstance(List<MutablePair<String, String>> orgs, String item) {
        ChooseOrgDialogFragment frag = new ChooseOrgDialogFragment();
        int size = orgs.size();
        String[] orgCodes = new String[size];
        String[] orgNames = new String[size];
        for (int i=0;i<size;i++){
            MutablePair<String,String> temp = orgs.get(i);
            orgCodes[i] = temp.getLeft();
            orgNames[i] = temp.getRight();
        }
        Bundle args = new Bundle();
        args.putString("item", item);
        args.putStringArray("codes", orgCodes);
        args.putStringArray("names",orgNames);
        frag.setArguments(args);
        return frag;
    }


    private String[] suoshujigouItems;
    private String[] suoshujigouIDs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String item = getArguments().getString("item");
        suoshujigouIDs = getArguments().getStringArray("codes");
        suoshujigouItems = getArguments().getStringArray("names");
        int position = ArrayUtils.indexOf(suoshujigouItems, item);

        return new AlertDialog.Builder(getActivity()).setTitle("选择所属机构")
                .setSingleChoiceItems(suoshujigouItems, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusProvider.getInstance().post(new ChooseOrgEvent(suoshujigouItems[which],suoshujigouIDs[which]));
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
