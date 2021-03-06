package com.kirat.solutions.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.util.CloudStorageConfig;
import com.kirat.solutions.util.FileItException;

public class BookTreeProcessor {

	public JSONObject processBookXmltoDoc(String bookName) throws Exception {

		String line = "", str = "";
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		JSONObject json;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
			CloudStorageConfig oCloudStorageConfig = new CloudStorageConfig(); 
			InputStream oInputStream = oCloudStorageConfig.getFile("1dvaultdata", "test.JSON");
			String requiredXmlPath = "";
			JSONParser parser = new JSONParser();
			JSONObject array = null;
			array = (JSONObject) parser.parse(new InputStreamReader(oInputStream));
			oInputStream.close();
			JSONArray jsonArray = (JSONArray) array.get("BookList");
			for (Object obj : jsonArray) {
				JSONObject book = (JSONObject) obj;
				if (book.containsKey(bookName)) {
					JSONObject jsonObject = (JSONObject) book.get(bookName);
					requiredXmlPath = (String) jsonObject.get("Path");
				}
			}
			BufferedReader br = null;
			InputStream xmlInputStream = oCloudStorageConfig.getFile("1dvaultdata", requiredXmlPath);
			br = new BufferedReader(new InputStreamReader(xmlInputStream));

			while ((line = br.readLine()) != null) {
				str += line;
			}
			br.close();
			org.json.JSONObject jsondata = XML.toJSONObject(str);

			json = (JSONObject) parser.parse(jsondata.toString());
		} catch (ParserConfigurationException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			throw new FileItException(e.getMessage());
		}
		return json;
	}

}