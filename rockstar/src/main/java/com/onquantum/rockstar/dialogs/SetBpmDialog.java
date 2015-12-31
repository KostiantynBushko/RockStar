package com.onquantum.rockstar.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;

import org.apache.http.impl.conn.tsccm.BasicPoolEntryRef;

/**
 * Created by Admin on 12/29/15.
 */
public class SetBpmDialog extends DialogFragment {

    private int BPM = 240;
    public interface SetBpmInterface {
        void OnSetBpm(int bpm);
        void OnCancelDialog();
    }
    private SetBpmInterface setBpmInterface;
    public void SetBpmListener(SetBpmInterface setBpmInterface) {
        this.setBpmInterface = setBpmInterface;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BPM = new Settings(getActivity()).getBPM();
        View view = getActivity().getLayoutInflater().inflate(R.layout.select_bpm_rate_dialog,null);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.bpmRadioGroup);
        switch (BPM) {
            case 240 : {
                ((RadioButton)view.findViewById(R.id.radioButton1)).setChecked(true);
                break;
            }
            case 120 : {
                ((RadioButton)view.findViewById(R.id.radioButton2)).setChecked(true);
                break;
            }
            case 80 : {
                ((RadioButton)view.findViewById(R.id.radioButton3)).setChecked(true);
                break;
            }
            case 60 : {
                ((RadioButton)view.findViewById(R.id.radioButton5)).setChecked(true);
                break;
            }
            case 30 : {
                ((RadioButton)view.findViewById(R.id.radioButton4)).setChecked(true);
                break;
            }default:break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1 :{
                        BPM = 240;
                        break;
                    }
                    case R.id.radioButton2 : {
                        BPM = 120;
                        break;
                    }
                    case R.id.radioButton3 : {
                        BPM = 80;
                        break;
                    }
                    case R.id.radioButton5 : {
                        BPM = 60;
                        break;
                    }
                    case R.id.radioButton4 : {
                        BPM = 30;
                        break;
                    }
                    default: break;
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tempo");
        builder.setView(view);
        builder.setIcon(R.drawable.ic_metronom_white);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Settings(getActivity()).setBPM(BPM);
                if(setBpmInterface != null)
                    setBpmInterface.OnSetBpm(BPM);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(setBpmInterface != null)
            setBpmInterface.OnCancelDialog();
    }
}
