package oldking.netdemo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by OldKing on 2018/6/13 0013.
 */

public class DetailActivity extends Activity {
    private View v_back, v_download, v_play_local, v_play_cloud, v_stop;
    private TextView tv_content;

    private String path_txt;//文本文件路径
    private String content;//读取的文本内容
    private String path_pcm;//音频文件路径

    private KProgressHUD dialog;

    // 语音合成对象
    private SpeechSynthesizer mTts;//text to speech

    private boolean isPlaying = false;

    private static final String AUDIO_TYPE = ".pcm";
    private static final int FREQUENCY = 16000;
    private static final int PLAY_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        path_txt = getIntent().getStringExtra("path");
        ToastUtils.showShort(path_txt);

        v_back = findViewById(R.id.back);
        tv_content = findViewById(R.id.content);
        v_download = findViewById(R.id.download);
        v_play_local = findViewById(R.id.play_local);
        v_play_cloud = findViewById(R.id.play_cloud);
        v_stop = findViewById(R.id.stop);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        v_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTts.stopSpeaking();
                if (!TextUtils.isEmpty(content)) {
                    showLoadingDialog();
                    int code = mTts.synthesizeToUri(content, path_pcm, new SynthesizerListener() {
                        @Override
                        public void onSpeakBegin() {

                        }

                        @Override
                        public void onBufferProgress(int i, int i1, int i2, String s) {

                        }

                        @Override
                        public void onSpeakPaused() {

                        }

                        @Override
                        public void onSpeakResumed() {

                        }

                        @Override
                        public void onSpeakProgress(int i, int i1, int i2) {

                        }

                        @Override
                        public void onCompleted(SpeechError speechError) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onEvent(int i, int i1, int i2, Bundle bundle) {

                        }
                    });
                    if (code == ErrorCode.SUCCESS) {
                        ToastUtils.showShort(path_pcm);
                        v_download.setEnabled(false);
                        v_play_local.setEnabled(true);
                    } else {
                        FileUtils.deleteFile(path_pcm);
                        ToastUtils.showShort("语音合成失败,错误码:" + code);
                    }
                }
            }
        });
        v_play_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying && !mTts.isSpeaking()) {
                    playLocal();
                }
            }
        });
        v_play_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTts.isSpeaking() && !isPlaying) {
                    if (!TextUtils.isEmpty(content)) {
                        showLoadingDialog();
                        mTts.startSpeaking(content, synthesizerListener);
                    }
                }
            }
        });
        v_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTts.stopSpeaking();
                isPlaying = false;
            }
        });

        dialog = KProgressHUD.create(this);
        showLoadingDialog();

        new ReadFileTask().execute();
    }

    private void playLocal() {
        isPlaying = true;
        new PlayPCMTask().execute();
    }

    private class PlayPCMTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
//            int bufferSize = AudioTrack.getMinBufferSize(FREQUENCY, PLAY_CHANNEL, AUDIO_ENCODING);
//            short[] buffer = new short[bufferSize];
//            try {
//                // 定义输入流，将音频写入到AudioTrack类中，实现播放
//                DataInputStream dis = new DataInputStream(
//                        new BufferedInputStream(new FileInputStream(path_pcm)));
//                // 实例AudioTrack
//                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, PLAY_CHANNEL, AUDIO_ENCODING,
//                        bufferSize, AudioTrack.MODE_STREAM);
//                // 开始播放
//                track.play();
//                // 由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
//                while (isPlaying && dis.available() > 0) {
//                    int i = 0;
//                    while (dis.available() > 0 && i < buffer.length) {
//                        buffer[i] = dis.readShort();
//                        i++;
//                    }
//                    // 然后将数据写入到AudioTrack中
//                    track.write(buffer, 0, buffer.length);
//                }
//                // 播放结束
//                track.stop();
//                track.release();
//                dis.close();
//            } catch (Exception e) {
//                Log.e("pcm", e.getMessage());
//            }
            // 获得构建对象的最小缓冲区大小
            int minBufferSize = AudioTrack.getMinBufferSize(FREQUENCY, PLAY_CHANNEL, AUDIO_ENCODING);
            // 较优播放块大小
            int primePlaySize = minBufferSize * 2;
            // 实例AudioTrack
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, PLAY_CHANNEL, AUDIO_ENCODING,
                    minBufferSize, AudioTrack.MODE_STREAM);
            // 当前播放位置
            int playOffset = 0;
            // 转化音频流
            byte[] data = FileIOUtils.readFile2BytesByStream(path_pcm);
            // 开始播放
            track.play();
            while (true) {
                if (!isPlaying) {
                    break;
                }
                try {
                    track.write(data, playOffset, primePlaySize);
                    playOffset += primePlaySize;
                } catch (Exception e) {
                    Log.e("pcm", e.getMessage());
                    break;
                }
                if (playOffset >= data.length) {
                    break;
                }
            }
            // 播放结束
            track.stop();
            track.release();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            isPlaying = false;
        }
    }

    private void showLoadingDialog() {
        dialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTts != null) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        isPlaying = false;
    }

    private void initSpeech() {
        SpeechUtility.createUtility(DetailActivity.this, "appid=5b066986");
        mTts = SpeechSynthesizer.createSynthesizer(DetailActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                dialog.dismiss();
                if (i == ErrorCode.SUCCESS) {
                    setParams();
                    judgeLocal();
                } else {
                    ToastUtils.showShort("语音合成初始化失败,错误码:" + i);
                }
            }
        });
    }

    private void judgeLocal() {
        path_pcm = path_txt.replace(".txt", AUDIO_TYPE);
        if (FileUtils.isFileExists(path_pcm)) {
            v_download.setEnabled(false);
            v_play_local.setEnabled(true);
        } else {
            v_download.setEnabled(true);
            v_play_local.setEnabled(false);
        }
        v_play_cloud.setEnabled(true);
        v_stop.setEnabled(true);
    }

    private String getFileName(String path) {
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return path.substring(start + 1, end);
        } else {
            return null;
        }
    }

    private void setParams() {
        //清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //引擎类型
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, String.valueOf(AudioManager.STREAM_MUSIC));
        //设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            dialog.dismiss();
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private class ReadFileTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            content = FileIOUtils.readFile2String(path_txt);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            initSpeech();
            tv_content.setText(content);
        }
    }
}
