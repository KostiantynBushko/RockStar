package fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/31/14.
 */
public class SettingsFragment extends Fragment {
    public static final String SETTINGS_FRAGMENT = "settings_fragment";
    private View root;
    Fragment currentFragment;
    ViewGroup viewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = this;
        this.viewGroup = container;

        root = inflater.inflate(R.layout.settings_fragment, container, false);
        ((Button)root.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info"," Close current fragment");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(currentFragment);
                transaction.commit();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        Log.i("info"," SettingsFragment" + " onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("info"," SettingsFragment" + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("info"," SettingsFragment" + " onDestroy");
        super.onDestroy();
    }

}
