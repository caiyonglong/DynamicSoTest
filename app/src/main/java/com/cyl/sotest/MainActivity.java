package com.cyl.sotest;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

/**
 * 动态加载so文件
 */
public class MainActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method
        tv = findViewById(R.id.sample_text);
        // Example of a call to a native method
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] abis = Build.SUPPORTED_ABIS;
                for (String abi : abis) {
                    Log.e("SO_ABI", "SUPPORTED_ABIS =============> " + abi);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //目标目录
                        File targetDir = MainActivity.this.getDir("test_libs", Context.MODE_PRIVATE);
                        String targetFilePath = targetDir + "/libnative-lib.so";
                        copySoFromAssetsToLibs("soo", targetFilePath, true);
                    }
                }).start();
            }
        });
    }

    /**
     * 从asset中复制so到目标文件
     */
    public void copySoFromAssetsToLibs(String soFilePath, final String targetFilePath, boolean encrypted) {
        String cKey = "abcdefghabcdefgh";
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(soFilePath);
            //解密
            if (encrypted) {
                AESHelper.decryptFile(cKey, inputStream, new File(targetFilePath));
            }
            DealSoHelper.getInstance(MainActivity.this).loadSo(new DealSoHelper.LoadListener() {
                @Override
                public void finish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(stringFromJNI());
                        }
                    });
                }
            });
        } catch (Throwable e) {
        } finally {
        }
    }

    public native String stringFromJNI();
}
