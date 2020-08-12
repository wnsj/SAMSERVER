package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaymentService;
import com.jiubo.sam.util.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Struct;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @desc:
 * @date: 2019-09-12 16:04
 * @author: dx
 * @version: 1.0
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
class PaymentServiceImplTest {

//    @Autowired
//    private PaymentService paymentService;
//
//    @Test
//    public void queryGatherPayment() throws Exception {
//        //JSONObject jsonObject = paymentService.queryGatherPayment(null);
//        //JSONObject jsonObject = paymentService.queryPaymentList(null);
//        //System.out.println(jsonObject.toJSONString());
//        paymentService.queryPatient(null);
//    }

    public static void main(String[] args) throws Exception {
        // testReadAndWriteNIO();
        String inputFile = "E:\\software\\CentOS-7-x86_64-DVD-1708.iso";
        String outFile = "E:\\software\\CentOS-7-BACK.iso";
        nioCopy2(inputFile, outFile);
        //testReadAndWriteNIO2();
    }

    //耗时:3分钟15秒503毫秒
    public static void testReadAndWriteNIO() {
        long startTimeMillis = System.currentTimeMillis();
        FileInputStream fin = null;
        FileOutputStream fos = null;
        String inputFile = "E:\\software\\CentOS-7-x86_64-DVD-1708.iso";
        String outFile = "E:\\software\\CentOS-7-BACK.iso";
        try {
            fin = new FileInputStream(new File(inputFile));
            FileChannel channel = fin.getChannel();

            int capacity = 1024;// 字节
            ByteBuffer bf = ByteBuffer.allocate(capacity);
            System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity() + "位置是：" + bf.position());
            int length = -1;

            fos = new FileOutputStream(new File(outFile));
            FileChannel outchannel = fos.getChannel();


            while ((length = channel.read(bf)) != -1) {
                //将当前位置置为limit，然后设置当前位置为0，也就是从0到limit这块，都写入到同道中
                bf.flip();

                int outlength = 0;
                while ((outlength = outchannel.write(bf)) != 0) {
                    System.out.println("读，" + length + "写," + outlength);
                }

                //将当前位置置为0，然后设置limit为容量，也就是从0到limit（容量）这块，
                //都可以利用，通道读取的数据存储到
                //0到limit这块
                bf.clear();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("耗时:" + TimeUtil.getTimeCHStr(System.currentTimeMillis() - startTimeMillis));
    }

    //文件内存映射及文件通道方式
    private static void nioCopy2(String sourcePath, String destPath) throws Exception {
        long startTimeMillis = System.currentTimeMillis();

        FileChannel sourceCh = FileChannel.open(Paths.get(sourcePath), StandardOpenOption.READ);
        FileChannel destCh = FileChannel.open(Paths.get(destPath), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf = sourceCh.map(FileChannel.MapMode.READ_ONLY, 0, sourceCh.size());
        MappedByteBuffer outMappedBuf = destCh.map(FileChannel.MapMode.READ_WRITE, 0, destCh.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        sourceCh.close();
        destCh.close();
        System.out.println("耗时:" + TimeUtil.getTimeCHStr(System.currentTimeMillis() - startTimeMillis));
    }

    //耗时:2分钟25秒657毫秒,3分钟5秒60毫秒
    public static void testReadAndWriteNIO2() {
        long startTimeMillis = System.currentTimeMillis();
        FileInputStream fin = null;
        FileOutputStream fos = null;
        String inputFile = "E:\\software\\CentOS-7-x86_64-DVD-1708.iso";
        String outFile = "E:\\software\\CentOS-7-BACK.iso";
        try {
            fin = new FileInputStream(new File(inputFile));
            FileChannel channel = fin.getChannel();
            fos = new FileOutputStream(new File(outFile));
            FileChannel outchannel = fos.getChannel();
            long length = channel.size();
            while (length > 0) {
                length -= channel.transferTo(channel.position(), length, outchannel);
            }
            //or
//            while (length > 0) {
//                //transferFrom并不会改变out流的position位置，
//                //但是会改变的input流的position位置；
//                length -= outchannel.transferFrom(channel, channel.position(), length);
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("耗时:" + TimeUtil.getTimeCHStr(System.currentTimeMillis() - startTimeMillis));
    }
}
