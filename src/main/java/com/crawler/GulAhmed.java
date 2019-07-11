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
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AbstractDocument.BranchElement;

public class GulAhmed {
	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from links where  links<>'' and site_name='Gull Ahmed' and category='men'");
		
		while (rs.next()) {

			String link = rs.getString(2);
			String brand_name = rs.getString(3);
			getDetailPage(link, brand_name);
		}
		/*for(int i=1; i< 15; i++){
			getListing("https://www.gulahmedshop.com/sale/men?p="+i) ;
		}*/
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
		Elements products = document.select("#category-products-grid ol li");

		for (Element e : products) {

			String aHref = e.select("a").attr("href");
			System.out.println(aHref);
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("select count(1) from links where links='" + aHref + "'");
			while (resultSet.next()) {
				int count = resultSet.getInt(1);
				if (count == 0) {
					String sql = "insert into links (links, site_name, category) values(?,?,?);";
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, aHref);
					preparedStmt.setString(2, "Gull Ahmed");
					preparedStmt.setString(3, "men");
					preparedStmt.execute();
				} else {
					System.out.println("Link already exists");
				}
			}
		}
	}

	public static void getDetailPage(String URL, String s_brand_name) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();

		Elements product = document.select("div[class=product-main-content]");
		String s_title = product.select("h1[class=page-title]").text();

		String s_sku = product.select("div[class=product attribute sku]").text();
		String s_instock = product.select("div[class=stock available]").text();

		String s_description = "Discription not available";
		String s_disclaimer = "";
		String s_delivery = "";

		String s_org_price = product.select("span[id^=old-price-]").text();
		String s_dis_price = product.select("span[id^=product-price-]").text();
		Elements div = document.select("div[class^=MagicToolboxSelectors]").select("a");
		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			imgDirectoy.put("s_pic_" + i++, e.attr("href"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
		String s_pic_2 = (String) imgDirectoy.get("s_pic_2");
		String s_pic_3 = (String) imgDirectoy.get("s_pic_3");
		String s_pic_4 = (String) imgDirectoy.get("s_pic_4");

		String sql = "insert into listing (s_sku, s_instock,s_description, s_disclaimer, s_delivery, "
				+ "s_org_price, s_dis_price,s_pic_1,s_pic_2,s_pic_3,s_pic_4,s_detail_link,s_title,s_brand_name, s_category) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

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
		preparedStmt.setString(15, "men");
		preparedStmt.execute();

	}

}
