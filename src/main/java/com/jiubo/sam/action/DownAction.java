package com.jiubo.sam.action;

import org.apache.catalina.connector.ClientAbortException;

import org.apache.poi.util.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @desc:
 * @date: 2020-06-05 17:04
 * @author: dx
 * @version: 1.0
 */
@Controller
@RequestMapping("/downAction")
public class DownAction {

    //文件断点续传
    @RequestMapping("/downFile")
    public void downFile(HttpServletRequest request, HttpServletResponse response) {
        download(request, response, new File(request.getParameter("path")));
    }

    //NIO断点续传
    @RequestMapping("/downFileTwo")
    public void downFileTwo(HttpServletRequest request, HttpServletResponse response) {
       // download2(request, response, new File(request.getParameter("path")));
    }

    //mvc文件下载
    @RequestMapping("/mvcDown")
    public ResponseEntity mvcDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
        File file = new File(request.getParameter("path"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=".concat(URLEncoder.encode(file.getName(), "UTF-8")));
        //设置MIME类型
        //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpStatus statusCode = HttpStatus.OK;
        FileSystemResource fsr = new FileSystemResource(file);
        return ResponseEntity
                .status(statusCode)
                .headers(headers)
                .contentLength(file.length())
                .body(fsr);
    }

    //IO文件下载
    @RequestMapping("/downFile2")
    public void downFile2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(request.getParameter("path"));
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment; filename=".concat(URLEncoder.encode(file.getName(), "UTF-8")));
            //response.setBufferSize(Integer.parseInt(String.valueOf(file.length())));
            IOUtils.copy(bis, response.getOutputStream());
        } catch (ClientAbortException e) {
            System.out.println("客户取消下载...");
        } finally {
            if (bis != null) bis.close();
            if (fis != null) fis.close();
        }
    }

    //NIO文件下载
    @RequestMapping("/fileChannel")
    public void fileChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            File file = new File(request.getParameter("path"));
            fis = new FileInputStream(file);
            channel = fis.getChannel();

            int capacity = 4092;// 字节
            ByteBuffer bf = ByteBuffer.allocate(capacity);
            System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity() + "位置是：" + bf.position());
            int length = -1;
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment; filename=".concat(URLEncoder.encode(file.getName(), "UTF-8")));

            while ((length = channel.read(bf)) != -1) {
                //将当前位置置为limit，然后设置当前位置为0，也就是从0到limit这块，都写入到同道中
                bf.flip();
                outputStream.write(bf.array(), 0, length);
                bf.clear();
            }
        } catch (ClientAbortException e) {
            System.out.println("客户取消下载...");
        } finally {
            if (channel != null) channel.close();
            if (fis != null) fis.close();
        }
    }


    /**
     * 下载服务器已存在的文件,支持断点续传
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param proposeFile 文件
     */
    public void download(HttpServletRequest request, HttpServletResponse response, File proposeFile) {
        InputStream inputStream = null;
        OutputStream bufferOut = null;
        try {
            // 设置响应报头
            long fSize = proposeFile.length();
            response.setContentType("application/x-download");
            // Content-Disposition: attachment; filename=WebGoat-OWASP_Developer-5.2.zip
            response.addHeader("Content-Disposition", "attachment; filename=".concat(URLEncoder.encode(proposeFile.getName(), "UTF-8")));
            // Accept-Ranges: bytes
            response.setHeader("Accept-Ranges", "bytes");
            //pos开始读取位置;
            long pos = 0;
            //last最后读取位置;
            long last = fSize - 1;
            // sum记录总共已经读取了多少字节
            long sum = 0;
            String range = request.getHeader("Range");
            if (null != range) {
                // 断点续传
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                try {
                    // 情景一：RANGE: bytes=2000070- 情景二：RANGE: bytes=2000070-2000970
                    String numRang = range.replaceAll("bytes=", "");
                    String[] strRange = numRang.split("-");
                    if (strRange.length == 2) {
                        pos = Long.parseLong(strRange[0].trim());
                        last = Long.parseLong(strRange[1].trim());
                    } else {
                        pos = Long.parseLong(numRang.replaceAll("-", "").trim());
                    }
                } catch (NumberFormatException e) {
                    System.out.println(range + " is not Number!");
                    pos = 0;
                }
            }
            long rangLength = last - pos + 1;// 总共需要读取的字节
            // Content-Range: bytes 10-1033/304974592
            String contentRange = new StringBuffer("bytes ").append(pos).append("-").append(last).append("/").append(fSize).toString();
            response.setHeader("Content-Range", contentRange);
            // Content-Length: 1024
            response.addHeader("Content-Length", String.valueOf(rangLength));
            response.setBufferSize(((Long) rangLength).intValue());

            // 跳过已经下载的部分，进行后续下载
            bufferOut = new BufferedOutputStream(response.getOutputStream());
            inputStream = new BufferedInputStream(new FileInputStream(proposeFile));
            inputStream.skip(pos);
            byte[] buffer = new byte[4092];
            int length = 0;
            while (sum < rangLength) {
                length = inputStream.read(buffer, 0, ((rangLength - sum) <= buffer.length ? ((int) (rangLength - sum)) : buffer.length));
                sum = sum + length;
                bufferOut.write(buffer, 0, length);
            }
        } catch (Throwable e) {
            if (e instanceof ClientAbortException) {
                // 浏览器点击取消
                System.out.println("用户取消下载!");
            } else {
                System.out.println("下载文件失败....");
                e.printStackTrace();
            }
        } finally {
            try {
                if (bufferOut != null) {
                    bufferOut.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

