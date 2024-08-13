package com.joysuch.document.common;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class POIUtil {

	/**
	 * @Description: POI 读取 word
	 * @create: 2019 - 07 - 27 9:48
	 * @update logs
	 * @throws Exception
	 */
	public static List<String> readWord(String filePath) throws Exception {
		List<String> linList = new ArrayList<String>();
		String buffer = "";
		try {
			if (filePath.endsWith(".doc")) {
				try (InputStream is = new FileInputStream(new File(filePath));
					 WordExtractor ex = new WordExtractor(is)) {
					buffer = ex.getText();
					if (buffer.length() > 0) {
						// 使用回车换行符分割字符串
						String[] arry = buffer.split("\\n");
						for (String string : arry) {
							linList.add(string.trim());
						}
					}
				}
			} else if (filePath.endsWith(".docx")) {
				try (OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
					 POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage)) {
					buffer = extractor.getText();
					if (buffer.length() > 0) {
						// 使用换行符分割字符串
						String[] arry = buffer.split("\\n");
						for (String string : arry) {
							linList.add(string.trim());
						}
					}
				}
			} else {
				return null;
			}

			return linList;
		} catch (Exception e) {
			System.out.print("error---->" + filePath);
			e.printStackTrace();
			return null;
		}
	}
}