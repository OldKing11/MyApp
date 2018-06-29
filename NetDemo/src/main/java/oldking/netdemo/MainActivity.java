package oldking.netdemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by OldKing on 2018/6/13 0013.
 */

public class MainActivity extends Activity {
    private ListView lv;
    private View progressbar;

    private DownloadReceiver receiver;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv);
        progressbar = findViewById(R.id.progressbar);

        receiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter("download.finish");
        registerReceiver(receiver, filter);

        dialog = KProgressHUD.create(this);

        requestPermissions();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.97.186.229:82/file-uplaod/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(IFilesGet.class).getFiles().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.body() != null) {
                    freshUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                ToastUtils.showShort("请求失败");
            }
        });
    }

    private interface IFilesGet {
        @GET("listtxt.php")
        Call<java.util.List<String>> getFiles();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void freshUI(List<String> files) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this, R.layout.item_file, R.id.tv,
                files.toArray(new String[files.size()]));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getAdapter().getItem(position);
//                String path = getExternalCacheDir().getPath() + name;
                String path = getExternalFilesDir("download").getPath() + "/" +name;
                File file = new File(path);
                if (file.exists()) {
                    toDetail(path);
                } else {
                    dialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                    Intent it = new Intent(MainActivity.this, DownloadService.class);
                    it.putExtra("name", name);
                    startService(it);
                }
            }
        });
        progressbar.setVisibility(View.GONE);
    }

    private void toDetail(String path) {
        Intent it = new Intent(MainActivity.this, DetailActivity.class);
        it.putExtra("path", path);
        startActivity(it);
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            dialog.dismiss();
            String path = intent.getStringExtra("path");
            if (TextUtils.isEmpty(path)) {
                ToastUtils.showShort("下载失败");
            } else {
                toDetail(path);
            }
        }
    }
}
