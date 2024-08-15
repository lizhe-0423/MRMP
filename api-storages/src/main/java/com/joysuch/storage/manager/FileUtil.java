package com.joysuch.storage.manager;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: LiHuaZhi
 * @Date: 2022/1/19 13:54
 * @Description:
 **/
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static MimetypesFileTypeMap mimetypesFileTypeMap;

    /**
     * 下载http文件流
     *
     * @param urlStr
     * @param request
     * @param response
     * @param fileName
     * @return
     */
    public static void downloadHttpFile(String urlStr, HttpServletRequest request, HttpServletResponse response, String fileName) {
        ServletOutputStream out = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //得到输入流
            inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = FileUtil.inputStreamToByte(inputStream);
            // 下载
            out = response.getOutputStream();
            long contentLength = getData.length;
            FileUtil.setResponse(fileName, contentLength, request, response);
            out.write(getData);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("下载失败!");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //File、FileInputStream 转换为byte数组
    public static byte[] inputStreamToByte(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("文件转换失败!");
        }
    }

    /**
     * 下载文件流
     *
     * @param file
     * @param request
     * @param response
     * @param fileName
     * @return
     */
    public static void downloadFile(File file, HttpServletRequest request, HttpServletResponse response, String fileName) {
        if (file != null && file.exists() && file.length() > 0L) {
            try {
                RandomAccessFile randomFile = new RandomAccessFile(file, "r");
                Throwable var5 = null;

                Object var54;
                try {
                    ServletOutputStream out = response.getOutputStream();
                    Throwable var7 = null;

                    try {
                        long contentLength = randomFile.length();
                        String range = request.getHeader("Range");
                        long start = 0L;
                        long end = 0L;
                        if (range != null && range.startsWith("bytes=")) {
                            String[] values = range.split("=")[1].split("-");
                            start = Long.parseLong(values[0]);
                            if (values.length > 1) {
                                end = Long.parseLong(values[1]);
                            }
                        }

                        int requestSize;
                        if (end != 0L && end > start) {
                            requestSize = Long.valueOf(end - start + 1L).intValue();
                        } else {
                            requestSize = 2147483647;
                        }

                        FileUtil.setResponse(fileName, contentLength, request, response);

                        randomFile.seek(start);

                        byte[] buffer;
                        for (int needSize = requestSize; needSize > 0; needSize -= buffer.length) {
                            buffer = new byte[1024];
                            int len = randomFile.read(buffer);
                            if (needSize < buffer.length) {
                                out.write(buffer, 0, needSize);
                            } else {
                                out.write(buffer, 0, len);
                                if (len < buffer.length) {
                                    break;
                                }
                            }
                        }

                        out.flush();
                        var54 = null;
                    } catch (Throwable var47) {
                        var7 = var47;
                        throw var47;
                    } finally {
                        if (out != null) {
                            if (var7 != null) {
                                try {
                                    out.close();
                                } catch (Throwable var46) {
                                    var7.addSuppressed(var46);
                                }
                            } else {
                                out.close();
                            }
                        }

                    }
                } catch (Throwable var49) {
                    var5 = var49;
                    throw var49;
                } finally {
                    if (randomFile != null) {
                        if (var5 != null) {
                            try {
                                randomFile.close();
                            } catch (Throwable var45) {
                                var5.addSuppressed(var45);
                            }
                        } else {
                            randomFile.close();
                        }
                    }

                }
            } catch (IOException var51) {
                logger.debug(var51.getMessage(), var51);
                throw new RuntimeException(var51.getMessage());
            }
        } else {
            throw new RuntimeException("文件为空或不存在！");
        }
    }

    /**
     * @param fileName
     * @param contentLength
     * @param request
     * @param response
     * @return
     */
    public static void setResponse(String fileName, long contentLength, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType(FileUtil.getContentType("0.jpg"));
            boolean isPreview = "preview".equalsIgnoreCase(request.getParameter("source"));
            response.addHeader("Content-Disposition", (!isPreview ? "attachment; " : "") + "filename*=utf-8'zh_cn'" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Accept-Ranges", "bytes");

            String range = request.getHeader("Range");
            if (range == null) {
                response.setHeader("Content-Length", String.valueOf(contentLength));
            } else {
                response.setStatus(206);
                long requestStart = 0L;
                long requestEnd = 0L;
                String[] ranges = range.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Long.parseLong(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Long.parseLong(rangeDatas[1]);
                    }
                }

                long length = 0L;
                if (requestEnd > 0L) {
                    length = requestEnd - requestStart + 1L;
                    response.setHeader("Content-Length", String.valueOf(length));
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
                } else {
                    length = contentLength - requestStart;
                    response.setHeader("Content-Length", String.valueOf(length));
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1L) + "/" + contentLength);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("response响应失败!");
        }
    }

    public static String getContentType(String fileName) {
        if (mimetypesFileTypeMap == null) {
            mimetypesFileTypeMap = new MimetypesFileTypeMap();
        }

        return mimetypesFileTypeMap.getContentType(fileName);
    }

    /**
     * 将磁盘的多个文件打包成压缩包并输出流下载
     *
     * @param pathList 包含文件路径和名称的列表，每个元素是一个包含文件路径和名称的Map
     * @param request  HTTP请求对象，用于获取客户端信息
     * @param response HTTP响应对象，用于设置响应头和发送数据给客户端
     */
    public static void zipDirFileToFile(List<Map<String, String>> pathList, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 设置response参数并且获取ServletOutputStream
            ZipArchiveOutputStream zous = getServletOutputStream(response);

            // 遍历文件路径列表
            for (Map<String, String> map : pathList) {
                // 获取文件名
                String fileName = map.get("name");
                // 根据路径创建File对象
                File file = new File(map.get("path"));
                // 创建文件输入流
                InputStream inputStream = new FileInputStream(file);
                // 设置文件名、输入流和输出流，用于压缩文件
                setByteArrayOutputStream(fileName, inputStream, zous);
            }
            // 关闭压缩输出流
            zous.close();
        } catch (Exception e) {
            // 输出异常信息
            e.printStackTrace();
        }
    }

    /**
     * 将网络url资源文件的多个文件打包成压缩包并输出流下载
     *
     * @param pathList
     * @param request
     * @param response
     */
    public static void zipUrlToFile(List<Map<String, String>> pathList, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 设置response参数并且获取ServletOutputStream
            ZipArchiveOutputStream zous = getServletOutputStream(response);

            for (Map<String, String> map : pathList) {
                String fileName = map.get("name");
                InputStream inputStream = getInputStreamFromUrl(map.get("path"));
                setByteArrayOutputStream(fileName, inputStream, zous);
            }
            zous.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ZipArchiveOutputStream getServletOutputStream(HttpServletResponse response) throws Exception {

        String outputFileName = "文件" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";
        response.reset();
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(outputFileName, "UTF-8"));
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        ServletOutputStream out = response.getOutputStream();

        ZipArchiveOutputStream zous = new ZipArchiveOutputStream(out);
        zous.setUseZip64(Zip64Mode.AsNeeded);
        return zous;
    }

    private static void setByteArrayOutputStream(String fileName, InputStream inputStream, ZipArchiveOutputStream zous) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        byte[] bytes = baos.toByteArray();

        //设置文件名
        ArchiveEntry entry = new ZipArchiveEntry(fileName);
        zous.putArchiveEntry(entry);
        zous.write(bytes);
        zous.closeArchiveEntry();
        baos.close();
    }

    /**
     * 通过网络地址获取文件InputStream
     *
     * @param path 地址
     * @return
     */
    private static InputStream getInputStreamFromUrl(String path) {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
