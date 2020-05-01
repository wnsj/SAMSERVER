package com.jiubo.sam.action;

import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @desc:
 * @date: 2019-10-07 09:29
 * @author: dx
 * @version: 1.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/downloadAction")
public class DownloadAction {

    //文件下载（resources/template路径）
    @ResponseBody
    @RequestMapping("/downFile")
    public void downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = request.getParameter("fileName");
        if (StringUtils.isBlank(fileName)) throw new MessageException("文件名不能为空!");
        String path = request.getParameter("path");
        String resources = "template";
        if (StringUtils.isBlank(path)) {
            path = resources.concat(Constant.FILE_PARAM.VIRGULE).concat(fileName);
        } else {
            path = path.endsWith(Constant.FILE_PARAM.VIRGULE) ? path : path.concat(Constant.FILE_PARAM.VIRGULE);
            path = resources.concat(Constant.FILE_PARAM.VIRGULE).concat(path).concat(fileName);
        }

        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            //response.setCharacterEncoding("utf-8");
            String name = java.net.URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLDecoder.decode(name, "ISO-8859-1"));
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
            os = response.getOutputStream();
            bis = new BufferedInputStream(is);
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (FileNotFoundException e) {
            throw new MessageException("系统找不到指定的文件");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
