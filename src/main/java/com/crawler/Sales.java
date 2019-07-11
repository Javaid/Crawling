package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sales {

	public static Connection con = null;

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		connect();
		// getListing("https://www.zarashahjahan.com/pk/eid-collection-19.html");

		//getListing("https://www.zarashahjahan.com/pk/sale.html");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from links where id > 29");
		
		
		while (rs.next()) {

			String link = rs.getString(2);
			String brand_name = rs.getString(3);
			getDetailPage(link, brand_name);
		}
	}

	public static void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawling", "root", "");
			System.out.println(con);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void getListing(String URL) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();
		Elements products = document.select("#fme_layered_container ul li");

		for (Element e : products) {

			String aHref = e.select("a").attr("href");
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("select count(1) from links where links='" + aHref + "'");
			while (resultSet.next()) {
				int count = resultSet.getInt(1);
				if (count == 0) {
					String sql = "insert into links (links, site_name) values(?,?);";
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, aHref);
					preparedStmt.setString(2, "Zara Shahjahan");
					preparedStmt.execute();
				} else {
					System.out.println("Link already exists");
				}
			}
		}
	}

	public static void getDetailPage(String URL, String s_brand_name) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();

		Elements product = document.select("div[class=111 product-view col-md-12  col-sm-12 col-xs-12]");
		String s_title = product.select("div[class=product-name]").text();
		System.err.println(s_title);

		String s_sku = product.select("p[class=product-sku]").text();
		System.err.println(s_sku);
		String s_instock = product.select("p[class=availability in-stock]").text();

		System.out.println(s_instock);

		String s_description = product.select("div[class=short-description]").text();

		System.out.println(s_description);

		String s_disclaimer = product.select("div[class=std]").text();

		System.out.println(s_disclaimer);

		String s_delivery = product.select("div[class=short-description delivery]").text();

		System.out.println(s_delivery);

		String s_org_price = product.select("span[id^=old-price-]").text();

		System.out.println(s_org_price);

		String s_dis_price = product.select("span[id^=product-price-]").text();
		System.out.println(s_dis_price);

		Element div = document.select("div[class=more-views col-md-3 col-md-pull-9] ul").first();

		
		String s_pic_1 = (div.child(0)!= null)?div.child(0).select("a").attr("href"):"";
		String s_pic_2 = s_pic_1;
		String s_pic_3 = "";
		String s_pic_4 = "";
		/*System.out.println(div.childNodeSize());
		if(div.childNodeSize()==6){
			String s_pic_2 = div.child(1).select("a").attr("href") ;
			System.out.println(s_pic_2);
		}
		if(div.childNodeSize()==9){
			String s_pic_3 = div.child(2).select("a").attr("href") ;
		}	
		if(div.childNodeSize()==9){
			String s_pic_4 = div.child(3).select("a").attr("href") ;
		}*/
		

		Statement stmt = con.createStatement();
		ResultSet resultSet = stmt.executeQuery("select count(1) from listing where s_detail_link='" + URL + "'");
		while (resultSet.next()) {
			int count = resultSet.getInt(1);
			if (count == 0) {

				String sql = "insert into listing (s_sku, s_instock,s_description, s_disclaimer, s_delivery, "
						+ "s_org_price, s_dis_price,s_pic_1,s_pic_2,s_pic_3,s_pic_4,s_detail_link,s_title,s_brand_name) "
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

				PreparedStatement preparedStmt = con.prepareStatement(sql);
				preparedStmt.setString(1, s_sku);
				preparedStmt.setString(2, s_instock);
				preparedStmt.setString(3, s_description);
				preparedStmt.setString(4, s_disclaimer);
				preparedStmt.setString(5, s_delivery);
				preparedStmt.setString(6, s_org_price.replace("Rs. ", "").replace(",", ""));
				preparedStmt.setString(7, s_dis_price.replace("Rs. ", "").replace(",", ""));
				preparedStmt.setString(8, s_pic_1);
				preparedStmt.setString(9, s_pic_2);
				preparedStmt.setString(10, s_pic_3);
				preparedStmt.setString(11, s_pic_4);
				preparedStmt.setString(12, URL);
				preparedStmt.setString(13, s_title);
				preparedStmt.setString(14, s_brand_name);
				preparedStmt.execute();

			}

		}

	}

}
