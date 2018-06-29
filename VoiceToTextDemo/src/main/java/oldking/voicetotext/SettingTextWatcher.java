package oldking.voicetotext;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by OldKing on 2018/5/24 0024.
 */

public class SettingTextWatcher implements TextWatcher {
    private int editStart ;
    private int editCount ;
    private EditTextPreference mEditTextPreference;
    int minValue;//最小值
    int maxValue;//最大值
    private Context mContext;

    public SettingTextWatcher(Context context, EditTextPreference e, int min, int max) {
        mContext = context;
        mEditTextPreference = e;
        minValue = min;
        maxValue = max;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        editStart = start;
//        editCount = count;
        editStart = i;
        editCount = i2;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable)) {
            return;
        }
        String content = editable.toString();
        if (isNumeric(content)) {
            int num = Integer.parseInt(content);
            if (num > maxValue || num < minValue) {
                editable.delete(editStart, editStart + editCount);
                mEditTextPreference.getEditText().setText(editable);
                Toast.makeText(mContext, "超出有效值范围", Toast.LENGTH_SHORT).show();
            }
        } else {
            editable.delete(editStart, editStart + editCount);
            mEditTextPreference.getEditText().setText(editable);
            Toast.makeText(mContext, "只能输入数字哦", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 正则表达式-判断是否为数字
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
