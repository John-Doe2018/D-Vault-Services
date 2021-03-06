package com.kirat.solutions.processor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.simple.JSONObject;

import com.kirat.solutions.Constants.BinderConstants;
import com.kirat.solutions.domain.FileItContext;
import com.kirat.solutions.util.CloudStorageConfig;
import com.kirat.solutions.util.FileInfoPropertyReader;
import com.kirat.solutions.util.FileItException;

public class ContentProcessor {
	FileItContext fileItContext;
	List<String> paths = new ArrayList<String>();
	private static ContentProcessor INSTANCE;

	public static synchronized ContentProcessor getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ContentProcessor();
		}
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public JSONObject processContentImage(String bookName, InputStream inputFile, String path, String type)
			throws FileItException {
		JSONObject oJsonObject = new JSONObject();
		CloudStorageConfig oCloudStorageConfig = new CloudStorageConfig();
		PDDocument document = null;
		try {
			if (type.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
				XWPFDocument document1 = new XWPFDocument(OPCPackage.open(inputFile));
				PdfOptions options = PdfOptions.create();
				OutputStream out = new FileOutputStream(new File("./test.pdf"));
				PdfConverter.getInstance().convert(document1, out, options);
				BufferedImage bufferedImage = null;
				int pagecounter = 0;
				File newFIle = new File("./test.pdf");
				document = PDDocument.load(newFIle);
				newFIle.delete();
				List<PDPage> pages = document.getDocumentCatalog().getAllPages();
				for (PDPage page : pages) {
					pagecounter++;
					bufferedImage = page.convertToImage();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "gif", os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					oCloudStorageConfig.uploadFile("1dvaultdata", path + pagecounter + BinderConstants.IMG_EXTENSION,
							is, "image/jpeg");
					is.close();
				}
				oJsonObject.put("Success", "File Uploaded Successfully");
			} else {
				BufferedImage bufferedImage = null;
				int pagecounter = 0;
				document = PDDocument.load(inputFile);
				List<PDPage> pages = document.getDocumentCatalog().getAllPages();
				for (PDPage page : pages) {
					pagecounter++;
					bufferedImage = page.convertToImage();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "gif", os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					oCloudStorageConfig.uploadFile("1dvaultdata", path + pagecounter + BinderConstants.IMG_EXTENSION,
							is, "image/jpeg");
					is.close();
				}
				oJsonObject.put("Success", "File Uploaded Successfully");
			}
		} catch (IOException e) {
			throw new FileItException(e.getMessage());
		} catch (Exception e) {
			throw new FileItException(e.getMessage());
		}
		return oJsonObject;
	}

	public static String createDyanmicImagePath(int i, String bookName, String extension) {
		boolean isDirectory = false;
		String fullContentDirectory = null;
		String absoluteImgPath = null;

		String counter = String.valueOf(i);
		String staticPath = FileInfoPropertyReader.getInstance().getString("doc.static.path");
		fullContentDirectory = staticPath.concat("\\" + bookName + "\\Images");
		java.io.File file = new File(fullContentDirectory);
		isDirectory = file.isDirectory();
		if (!isDirectory) {
			file.mkdirs();
		}
		absoluteImgPath = fullContentDirectory.concat("\\" + counter.concat(extension));
		return absoluteImgPath;
	}

}