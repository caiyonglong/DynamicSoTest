package com.cyl.sotest;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * 作者：yonglong
 * 时间：2019/3/25 16:11
 * 描述：So文件处理，加密解密
 */
public class DealSoHelper {
    private static final String TARGET_LIBS_NAME = "test_libs";

    private static volatile DealSoHelper instance;

    private WeakReference<Context> weakReference;

    private DealSoHelper(Context context) {
        weakReference = new WeakReference<>(context);

    }

    public static DealSoHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DealSoHelper.class) {
                if (instance == null) {
                    instance = new DealSoHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 加载so文件
     */
    public void loadSo(LoadListener loadListener) {
        File dir = weakReference.get().getDir(TARGET_LIBS_NAME, Context.MODE_PRIVATE);
        File[] currentFiles;
        currentFiles = dir.listFiles();
        for (int i = 0; i < currentFiles.length; i++) {
            Log.e("SO", currentFiles[i].getAbsolutePath());
            System.load(currentFiles[i].getAbsolutePath());
        }
        loadListener.finish();
    }

    /**
     * @param fromFile 指定的本地目录
     * @param isCover  true覆盖原文件即删除原有文件后拷贝新的文件进来
     * @return
     */
    public void copySo(String fromFile, boolean isCover, CopyListener copyListener) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在,如果不存在则 return出去
        if (!root.exists()) {
            return;
        }
        //如果存在则获取当前目录下的全部文件并且填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = weakReference.get().getDir(TARGET_LIBS_NAME, Context.MODE_PRIVATE);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        } else {
            //删除全部老文件
            if (isCover) {
                for (File file : targetDir.listFiles()) {
                    file.delete();
                }
            }

        }
        //遍历要复制该目录下的全部文件¬
        for (int i = 0; i < currentFiles.length; i++) {

            if (currentFiles[i].getName().contains(".so")) {
                copySdcardFile(currentFiles[i].getPath(), targetDir.toString() + File.separator + currentFiles[i].getName());
            }
        }
        copyListener.finish();
    }


    /**
     * 文件拷贝(要复制的目录下的所有非文件夹的文件拷贝)
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    private static void copySdcardFile(String fromFile, String toFile) {
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fosfrom.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 从内存到写入到具体文件
            fosto.write(baos.toByteArray());
            // 关闭文件流
            baos.close();
            fosto.close();
            fosfrom.close();
        } catch (Exception ex) {
            return;
        }
    }

    /**
     * copy完成后回调接口
     */
    public interface CopyListener {
        //其实方法返回boolean也成
        void finish();
    }

    /**
     * load完成后回调接口
     */
    public interface LoadListener {
        //其实方法返回boolean也成
        void finish();
    }

    private static final String TAG = DealSoHelper.class.getSimpleName();

    public static void loadExSo(Context context,String soName, String soFilesDir){
        File soFile = choose(soFilesDir,soName);

        String destFileName = context.getDir("myso", Context.MODE_PRIVATE).getAbsolutePath()  + File.separator + soName;
        File destFile = new File(destFileName);
        if (soFile != null) {
            Log.e(TAG, "最终选择加载的so路径: " + soFile.getAbsolutePath());
            Log.e(TAG, "写入so的路径: " + destFileName);
            boolean flag = AESHelper.copyFile(soFile, destFile);
            if (flag) {
                System.load(destFileName);
            }
        }

    }

    /**
     * 在网络或者本地下载过的so文件夹: 选择适合当前设备的so文件
     *
     * @param soFilesDir so文件的目录, 如apk文件解压后的 Amusic/libs/ 目录 : 包含[arm64-v8a,arm64-v7a等]
     * @param soName so库的文件名, 如 libmusic.so
     * @return 最终匹配合适的so文件
     */
    private static File choose(String soFilesDir,String soName) {
        if (Build.VERSION.SDK_INT >= 21) {
            String [] abis = Build.SUPPORTED_ABIS;
            for (String abi : abis) {
                Log.e(TAG, "SUPPORTED_ABIS =============> " + abi);
            }
            for (String abi : abis) {
                File file = new File(soFilesDir,abi + File.separator + soName);
                if (file.exists()) {
                    return file;
                }
            }
        } else {
            File file = new File(soFilesDir, Build.CPU_ABI + File.separator + soName);
            if (file.exists()) {
                return file;
            } else {
                // 没有找到和Build.CPU_ABI 匹配的值,那么就委屈设备使用armeabi算了.
                File finnalFile = new File(soFilesDir, "armeabi" + File.separator + soName);
                if (finnalFile.exists()) {
                    return finnalFile;
                }
            }
        }
        return null;
    }

}