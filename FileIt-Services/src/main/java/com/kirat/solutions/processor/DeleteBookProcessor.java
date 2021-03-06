package com.kirat.solutions.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kirat.solutions.util.CloudStorageConfig;
import com.kirat.solutions.util.FileItException;

public class DeleteBookProcessor {

	@SuppressWarnings("unchecked")
	public JSONObject deleteBookProcessor(String deleteBookRequest) throws Exception {
		JSONObject parentObj = new JSONObject();
		JSONObject deleteMsg = new JSONObject();
		CloudStorageConfig oCloudStorageConfig = new CloudStorageConfig(); 
		InputStream oInputStream = oCloudStorageConfig.getFile("1dvaultdata", "test.JSON");
		JSONParser parser = new JSONParser();
		JSONObject array;
		try {
			array = (JSONObject) parser.parse(new InputStreamReader(oInputStream));
			oInputStream.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new FileItException(e.getMessage());
		}
		JSONArray jsonArray = (JSONArray) array.get("BookList");
		for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();) {
			JSONObject book = (JSONObject) iterator.next();
			if (book.containsKey(deleteBookRequest)) {
				iterator.remove();
				deleteMsg.put("Success", "Deleted Successfully");
				break;
			}
		}
		parentObj.put("BookList", jsonArray);
		try {
			InputStream is = new ByteArrayInputStream(parentObj.toJSONString().getBytes());
			oCloudStorageConfig.uploadFile("1dvaultdata", "test.JSON", is, "application/json");
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new FileItException(e.getMessage());
		}
		return deleteMsg;
	}

}