package com.cyl.sotest;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作者：yonglong
 * 时间：2019/3/12 19:41
 * 描述：Aes加密解密
 */
public class AESHelper {
    public static final String VIPARA = "0102030405060708";
    private static final String TAG = AESHelper.class.getSimpleName();

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    private static Cipher initAESCipher(String sKey, int cipherMode) {
        //创建Key gen
        KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(cipherMode, key, zeroIv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cipher;
    }


    /**
     * 对文件进行AES加密
     *
     * @param key
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static File encryptFile(String key, File sourceFile, File destFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
                //以加密流写入文件
                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = cipherInputStream.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                cipherInputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return destFile;
    }


    /**
     * AES方式解密文件
     *
     * @param key
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static File decryptFile(String key, File sourceFile, File destFile) {
        try {
            return decryptFile(key, new FileInputStream(sourceFile), destFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sourceFile;
    }

    public static File decryptFile(String key, InputStream inputStream, File destFile) {
        FileOutputStream out = null;
        try {
//            if (!destFile.getParentFile().exists()) {
//                destFile.getParentFile().mkdirs();
//            }
//            destFile.createNewFile();
            out = new FileOutputStream(destFile);
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return destFile;
    }


    public static boolean copyFile(File source, File target)  {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            FileChannel iChannel = inputStream.getChannel();
            FileChannel oChannel = outputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                buffer.clear();
                int r = iChannel.read(buffer);
                if (r == -1)
                    break;
                buffer.limit(buffer.position());
                buffer.position(0);
                oChannel.write(buffer);
            }
        } catch (Exception e) {
            return false;
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
        return true;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
