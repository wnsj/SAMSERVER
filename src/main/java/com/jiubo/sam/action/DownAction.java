package com.jiubo.sam.action;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @desc:
 * @date: 2020-06-05 17:04
 * @author: dx
 * @version: 1.0
 */
@Controller
@RequestMapping("/downAction")
public class DownAction {

    @RequestMapping("/downFile")
    public void downFile(HttpServletRequest request,HttpServletResponse response){
        download(request,response,new File("F:\\video\\meng\\springboot\\02-SpringBoot系统环境要求.mp4"));
    }

    /**
     * 下载服务器已存在的文件,支持断点续传
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param proposeFile  文件
     */
    public void download(HttpServletRequest request, HttpServletResponse response, File proposeFile) {
        InputStream inputStream = null;
        OutputStream bufferOut = null;
        try {
            // 设置响应报头
            long fSize = proposeFile.length();
            response.setContentType("application/x-download");
            // Content-Disposition: attachment; filename=WebGoat-OWASP_Developer-5.2.zip
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(proposeFile.getName(), "UTF-8"));
            // Accept-Ranges: bytes
            response.setHeader("Accept-Ranges", "bytes");
            long pos = 0, last = fSize - 1, sum = 0;//pos开始读取位置;  last最后读取位置;  sum记录总共已经读取了多少字节
            if (null != request.getHeader("Range")) {
                // 断点续传
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                try {
                    // 情景一：RANGE: bytes=2000070- 情景二：RANGE: bytes=2000070-2000970
                    String numRang = request.getHeader("Range").replaceAll("bytes=", "");
                    String[] strRange = numRang.split("-");
                    if (strRange.length == 2) {
                        pos = Long.parseLong(strRange[0].trim());
                        last = Long.parseLong(strRange[1].trim());
                    } else {
                        pos = Long.parseLong(numRang.replaceAll("-", "").trim());
                    }
                } catch (NumberFormatException e) {
                    System.out.println(request.getHeader("Range") + " is not Number!");
                    pos = 0;
                }
            }
            long rangLength = last - pos + 1;// 总共需要读取的字节
            // Content-Range: bytes 10-1033/304974592
            String contentRange = new StringBuffer("bytes ").append(pos).append("-").append(last).append("/").append(fSize).toString();
            response.setHeader("Content-Range", contentRange);
            // Content-Length: 1024
            response.addHeader("Content-Length", String.valueOf(rangLength));

            // 跳过已经下载的部分，进行后续下载
            bufferOut = new BufferedOutputStream(response.getOutputStream());
            inputStream = new BufferedInputStream(new FileInputStream(proposeFile));
            inputStream.skip(pos);
            byte[] buffer = new byte[8192];
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

