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

public class Generation {

	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from links where  links<>'' and site_name='Generation' and status=0");
		
		while (rs.next()) {

			String link = rs.getString(2);
			String brand_name = rs.getString(3);
			int id = rs.getInt(1);
			getDetailPage(id , link, brand_name);
		}
			//getListing("https://www.generation.com.pk/sale.html") ;
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
		Elements products = document.select(".category-products ul li");

		for (Element e : products) {

			String aHref = e.select("a.product-image").attr("href");
			System.out.println(aHref);
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery("select count(1) from links where links='" + aHref + "'");
			while (resultSet.next()) {
				int count = resultSet.getInt(1);
				if (count == 0) {
					String sql = "insert into links (links, site_name, category) values(?,?,?);";
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, aHref);
					preparedStmt.setString(2, "Generation");
					preparedStmt.setString(3, "Mix");
					preparedStmt.execute();
				} else {
					System.out.println("Link already exists");
				}
			}
		}
	}

	public static void getDetailPage(int id, String URL, String s_brand_name) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();

		Elements product = document.select("div[class=product-view]");
		String s_title = product.select("div[class=product-name]").text();

		String s_sku = product.select("p[class=product-sku]").text();
		String s_instock = product.select("p[class=availability in-stock]").text();

		String s_description = product.select("div[class=std]").text();
		String s_disclaimer = "";
		String s_delivery = "";

		String s_org_price = product.select("span[id^=old-price-]").text().replace("PKR ", "").replace(",", "");
		String org[]=  s_org_price.split("\\s");
		String s_dis_price = product.select("span[id^=product-price-]").text().replace("PKR ", "").replace(",", "");
		String dis[]=  s_dis_price.split("\\s");
		
		
		
		Elements div = document.select("div[class=MagicToolboxSelectorsContainer]").select("a");
		
		String s_pic_1 = document.select("span[id^=MagicZoomPlusImage]").attr("href");
		System.out.println(s_pic_1);
		Map imgDirectoy = new HashMap();
		int i = 2;
		for (Element e : div) {
			imgDirectoy.put("s_pic_" + i++, e.attr("href"));
		}

		//String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
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
		preparedStmt.setString(6, org[0]);
		preparedStmt.setString(7, dis[0]);
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
