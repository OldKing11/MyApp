package oldking.voicetotext;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.view.Window;

/**
 * Created by OldKing on 2018/5/24 0024.
 */

public class IatSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private EditTextPreference mVadbosPreference;
    private EditTextPreference mVadeosPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("OldKing");
        addPreferencesFromResource(R.xml.iat_setting);

        mVadbosPreference = (EditTextPreference) findPreference("iat_vadbos_preference");
        mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this, mVadbosPreference, 0, 10000));

        mVadeosPreference = (EditTextPreference) findPreference("iat_vadeos_preference");
        mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this, mVadeosPreference, 0, 10000));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }
}
