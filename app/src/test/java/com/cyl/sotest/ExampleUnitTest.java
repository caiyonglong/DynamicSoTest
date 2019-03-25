package com.cyl.sotest;

import org.junit.Test;

import java.io.File;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String cKey = "abcdefghabcdefgh";
        //未加密文件路径
        File oldfile = new File("F:\\project\\NdkTest\\app\\src\\main\\assets\\armeabi-v7a\\libnative-lib.so");
        //加密后的文件路径
        File encrypfile = new File("F:\\project\\NdkTest\\app\\src\\main\\assets\\soo");
        //解密后的文件路径
//        File decrypfile = new File("F:\\makemoney_client_va\\VirtualAppTest\\app\\src\\main\\assets\\icashgame2.apk");
        //加密文件
        AESHelper.encryptFile(cKey, oldfile, encrypfile);
        //解密文件
//        AESHelper.decryptFile(cKey, encrypfile, decrypfile);
    }
}