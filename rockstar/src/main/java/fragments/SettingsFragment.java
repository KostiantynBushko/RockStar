package fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.SwitchButton;

/**
 * Created by Admin on 8/31/14.
 */
public class SettingsFragment extends Fragment {
    private View root;
    Fragment currentFragment;
    ViewGroup viewGroup;

    private FragmentListener fragmentListener;
    private Context context;
    private Settings settings;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        currentFragment = this;
        this.viewGroup = container;
        this.context = getActivity().getApplicationContext();

        root = inflater.inflate(R.layout.settings_fragment, container, false);
        ((ImageButton)root.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(currentFragment);
                transaction.remove(currentFragment).commit();
                if (fragmentListener != null) {
                    fragmentListener.close();
                }
            }
        });

        SwitchButton button1 = (SwitchButton)root.findViewById(R.id.button);
        button1.Set(new Settings(context).isFretsNumberVisible());
        button1.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                settings.setFretNumberVisibility(isOn);
            }
        });

        SwitchButton button2 = (SwitchButton)root.findViewById(R.id.button2);
        button2.Set(new Settings(context).isFretsSliderVisible());
        button2.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(final boolean isOn) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(100);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                settings.setFretsSliderVisibility(isOn);
                            }
                        });
                    }
                }).start();
            }
        });

        SwitchButton button3 = (SwitchButton)root.findViewById(R.id.button3);
        button3.Set(new Settings(context).isTouchesVisible());
        button3.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                settings.setTouchesVisibility(isOn);
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

    public void setFragmentListener(FragmentListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    public void setSettingsInstance(Settings settings){ this.settings = settings; }
}
