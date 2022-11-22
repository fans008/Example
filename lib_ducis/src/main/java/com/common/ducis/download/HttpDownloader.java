package com.common.ducis.download;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @describe：下载工具类
 * @author：ftt
 * @date：2019/4/28
 */
public class HttpDownloader {

    public static String download(String docUrl, String path)throws Exception {
        File f = new File(path);
        if(!f.exists()){      //判断文件夹是否存在
            f.mkdir();        //如果不存在、则创建一个新的文件夹
        }
        String fileName = path + getFileName(docUrl,path);
        File file = new File(fileName);
        if(file.exists()){    //如果目标文件已经存在
            file.delete();    //则删除旧文件
        }
        //1K的数据缓冲
        byte[] bs = new byte[1024];
        //读取到的数据长度
        int len;
        try{
            //通过文件地址构建url对象
            URL url = new URL(docUrl);
            //获取链接
            //URLConnection conn = url.openConnection();
            //创建输入流
            InputStream is = url.openStream();
            //获取文件的长度
            //int contextLength = conn.getContentLength();
            //输出的文件流
            OutputStream os = new FileOutputStream(file);
            //开始读取
            while((len = is.read(bs)) != -1){
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
        }catch(MalformedURLException e){
            fileName = null;
            System.out.println("创建URL对象失败");
            throw e;
        }catch(FileNotFoundException e){
            fileName = null;
            System.out.println("无法加载文件");
            throw e;
        }catch(IOException e){
            fileName = null;
            System.out.println("获取连接失败");
            throw e;
        }
        return fileName;
    }

    public static String getFileName(String url, String path){
        //准备拼接新的文件名
        String[] list = url.split("/");
        StringBuffer filename = new StringBuffer();
        for (int i = 3; i < list.length-1; i++) {
            filename.append(list[i]);
            if (i != list.length-1) {
                File f = new File(path+filename);
                if(!f.exists()){      //判断文件夹是否存在
                    f.mkdir();        //如果不存在、则创建一个新的文件夹
                }
                filename.append("/");
            }
        }
        return filename.append(list[list.length-1]).toString();
    }
}