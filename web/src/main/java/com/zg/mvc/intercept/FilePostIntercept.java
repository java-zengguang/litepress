package com.zg.mvc.intercept;

import com.zg.mvc.util.io.IOUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class FilePostIntercept implements PostControllerIntercept {
    @Override
    public Object doInvoke(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException {
        if (args instanceof File) {
            File file = (File) args;
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + file.getName());
            OutputStream out = response.getOutputStream();
            int size = IOUtils.inputFile(out, file);
            Logger.info("文件下载完成");
        }
        return args;
    }
}
