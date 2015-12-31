package com.onquantum.rockstar.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import org.w3c.dom.Text;

/**
 * Created by Admin on 12/28/15.
 */
public class SaveFileDialog extends DialogFragment {

    public interface OnSaveFileListener {
        public void OnSaveFile(String fileName);
    }
    private OnSaveFileListener onSaveFileListener = null;
    public void SetOnSaveFileListener(OnSaveFileListener onSaveFileListener) {
        this.onSaveFileListener = onSaveFileListener;
    }

    private String fileName = null;
    private String title = "Save file";
    private EditText editText = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle.containsKey("file_name")) {
            fileName = bundle.getString("file_name");
        }
        if(bundle.containsKey("title")) {
            title = bundle.getString("title");
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_save_file, null);
        editText = (EditText)view.findViewById(R.id.fileName);
        if(fileName != null)
            editText.setText(fileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_save_white_48dp);
        builder.setTitle(title);
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editText != null && !editText.getText().toString().isEmpty())
                    fileName = editText.getText().toString();
                if(onSaveFileListener != null)
                    onSaveFileListener.OnSaveFile(fileName);
                dialog.cancel();
            }
        });
        return builder.create();
    }
}
