package oldking.netdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by OldKing on 2018/6/13 0013.
 */

public class DownloadService extends Service {
    private static String TAG = DownloadService.class.getSimpleName();

    private File dir;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
//        String path = getExternalCacheDir().getPath();
        String path = getExternalFilesDir("download").getPath();
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        String name = intent.getStringExtra("name");
        init(name);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    private void init(final String name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://47.97.186.229:82/file-uplaod/upload/")
                .build();
        retrofit.create(IFileDownload.class).downloadFile(name).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                saveFile(response, name);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtils.showShort("获取失败");
            }
        });
    }

    private void saveFile(Response<ResponseBody> response, String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            ToastUtils.showShort("已存在");
        } else {
            if (FileIOUtils.writeFileFromIS(file, response.body().byteStream())) {
                Intent it = new Intent("download.finish");
                it.putExtra("path", file.getAbsolutePath());
                sendBroadcast(it);
            } else {
                ToastUtils.showShort("下载失败");
            }
        }
        stopSelf();
    }

    private interface IFileDownload {
        @GET("{name}")
        Call<ResponseBody> downloadFile(@Path("name") String name);
    }
}
