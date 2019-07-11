package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Crawler {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter writer;
		FileWriter fWriter = new FileWriter("addressesUAE.txt");
		writer = new BufferedWriter(fWriter);
		for (int i = 0; i <= 61; i++) {
			getPageLinks("https://www.yellowpages.ae/s/uae/houses-"+i+"-1.html", writer);
		}

		writer.close();
	}

	public static void getPageLinks(String URL, BufferedWriter fWriter) {
		try {

			Document document = Jsoup.connect(URL).get();
			Elements addressOnPage = document.select("span[id^=ContentPlaceHolder1_grdListing_lblAddress1_]");

			
			for (Element addr : addressOnPage) {
				if (addr.text().length() > 0) {
					fWriter.write(addr.text()+ "\n\r");
					System.out.println(addr.text()+ "\n\r");
			
				}
			}
		} catch (IOException e) {
			System.err.println("For '" + URL + "': " + e.getMessage());
		}
	}

}
