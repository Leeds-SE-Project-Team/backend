package com.se.backend.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author FeianLing
 * @date 2019/9/16
 */
public class FileUtil {

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static boolean downloadFromUrl(String urlStr, String fileName, String savePath) {
        try {
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            inputStream.close();
//        System.out.println("info:" + url + " download success");
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    // 获取文件扩展名
    public static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex);
    }

    public static InputStream stringToInputStream(String content) {
        // 将字符串转换为字节数组
        byte[] bytes = content.getBytes();
        // 使用字节数组创建 ByteArrayInputStream
        return new ByteArrayInputStream(bytes);
        // 现在你可以使用 inputStream 对象进行其他操作，比如读取它的内容
        // 例如：
//        try {
//            int data;
//            while ((data = inputStream.read()) != -1) {
//                System.out.print((char) data);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 不要忘记关闭 InputStream
//            try {
//                inputStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    // 保存文件到本地
    public static void saveFileToLocal(InputStream inputStream, String uploadRelativePath) throws IOException {
        uploadRelativePath = "./static" + uploadRelativePath;
        try {
            File saveFile = new File(uploadRelativePath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
//            FileOutputStream os = new FileOutputStream(saveFile);
//            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (OutputStream outputStream = new FileOutputStream(uploadRelativePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}

