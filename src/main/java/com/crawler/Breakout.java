package com.crawler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Breakout {
	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();

		Statement stmt = con.createStatement();
		ResultSet rs = stmt
				.executeQuery("select * from links where  links<>'' and site_name='Breakout' and status=0;");

		while (rs.next()) {

			int id = rs.getInt(1);
			String link = rs.getString(2);
			String brand_name = rs.getString(3);
			getDetailPage(id, link, brand_name);
		}

		/*
		 * for(int i=30; i>= 1; i--){
		 * getListing("http://www.breakout.com.pk/sale?pagenumber="+i); }
		 */

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
		Elements products = document.select("#mkl div");

		for (Element e : products) {

			String aHref = e.select("div.picture a").attr("href");
			if (aHref != "") {
				System.out.println(aHref);
				String href = "http://www.breakout.com.pk" + aHref;
				Statement stmt = con.createStatement();
				ResultSet resultSet = stmt.executeQuery("select count(1) from links where links='" + href + "'");
				while (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count == 0) {
						String sql = "insert into links (links, site_name, category) values(?,?,?);";
						PreparedStatement preparedStmt = con.prepareStatement(sql);
						preparedStmt.setString(1, href);
						preparedStmt.setString(2, "Breakout");
						preparedStmt.setString(3, "Mix");
						preparedStmt.execute();
					} else {
						// System.out.println("Link already exists");
					}
				}
			} // check if href is empty
		}
	}

	public static void getDetailPage(int id, String URL, String s_brand_name) throws IOException, SQLException {
		System.out.println(URL);
		Document document = Jsoup.connect(URL).get();
		Elements product = document.select("div[class=product-essential]");
		String s_title = product.select("div[class=product-name] h1").text();

		String s_sku = product.select("span[id^=sku-]").text();
		String s_instock = "Available";

		String s_description = s_title;// product.select("div[class=col-lg-12
										// NormalText]").text();
		String s_disclaimer = "";
		String s_delivery = "";

		String s_org_price = product.select("div[class=old-product-price] span").text().replace("(PKR)", "")
				.replace(",", "");

		String s_dis_price = product.select("span[class^=price-value-]").text().replace("(PKR)", "").replace(",", "");

		Elements div = document.select("div[class=gallery picture]").select("img");
		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			System.out.println(e.attr("src"));
			imgDirectoy.put("s_pic_" + i++, e.attr("src"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
		String s_pic_2 = (String) imgDirectoy.get("s_pic_2");
		String s_pic_3 = (String) imgDirectoy.get("s_pic_3");
		String s_pic_4 = (String) imgDirectoy.get("s_pic_4");

		System.out.println(s_pic_4);

		String sql = "insert into listing (s_sku, s_instock,s_description, s_disclaimer, s_delivery, "
				+ "s_org_price, s_dis_price,s_pic_1,s_pic_2,s_pic_3,s_pic_4,s_detail_link,s_title,s_brand_name, s_category) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement preparedStmt = con.prepareStatement(sql);
		preparedStmt.setString(1, s_sku);
		preparedStmt.setString(2, s_instock);
		preparedStmt.setString(3, s_description);
		preparedStmt.setString(4, s_disclaimer);
		preparedStmt.setString(5, s_delivery);
		preparedStmt.setString(6, s_org_price);
		preparedStmt.setString(7, s_dis_price);
		preparedStmt.setString(8, s_pic_1);
		preparedStmt.setString(9, s_pic_2);
		preparedStmt.setString(10, s_pic_3);
		preparedStmt.setString(11, s_pic_4);
		preparedStmt.setString(12, URL);
		preparedStmt.setString(13, s_title);
		preparedStmt.setString(14, s_brand_name);
		preparedStmt.setString(15, "mix");
		 preparedStmt.execute();

		String sqlUpdate = "update links set status =1 where id=?";
		PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		preparedUpdateStmt.setInt(1, id);
		preparedUpdateStmt.executeUpdate();
	}
}
