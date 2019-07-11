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

public class Khaadi {

	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		 Statement stmt = con.createStatement(); ResultSet rs =
		  stmt.executeQuery(
		  "select * from listing where  s_brand_name='Khaadi'");
		  
		  while (rs.next()) {
		  int id = rs.getInt(1);
		  String link = rs.getString(11); 
		  System.out.println(link);
		  //String brand_name = rs.getString(3);
		  getListing(link); 
		  }
		 
		/*for (int i = 9; i >= 1; i--) {
			getListing("https://www.khaadi.com/pk/sale.html?p=" + i);
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
	public static void getDiscountedPrice(String URL) throws IOException{
		
		//https://www.khaadi.com/pk/hpc19213-default-19x29.html
	
		Document document = Jsoup.connect(URL).get();
		Elements product = document.select("div[class=product-item-info]");
		
	}
	
	

	public static void getListing(String URL) throws IOException, SQLException {

		Document document = Jsoup.connect(URL).get();
		Elements products = document.select("div[class^=products wrapper grid products-grid] ol li");
		//System.out.println(products);
		for (Element e : products) {

			//String aHref = e.select(".product-item-info a").attr("href");
			
			String[] price = e.select("div[class^=price-box price-final_price").text().split("\\s");
			
			String s_org_price =  price[0].replace("PKR", "");
			
			
			Statement stmt = con.createStatement();
			//ResultSet resultSet = stmt.executeQuery("select count(1) from links where links='" + s_org_price + "'");
			//while (resultSet.next()) {
				//int count = resultSet.getInt(1);
				//if (count == 0) {
				/*	String sql = "insert into links (links, site_name, category) values(?,?,?);";
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, aHref);
					preparedStmt.setString(2, "Khaadi");
					preparedStmt.setString(3, "Mix");
					 preparedStmt.execute();*/
					
					
					String sql = "update listing set s_org_price=? where s_detail_link='"+ URL +"'";
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, s_org_price);
					preparedStmt.execute();
					
					
				//} else {
				//	System.out.println("Link already exists");
				//}
			//}
		}
	}

	public static void getDetailPage(int id, String URL, String s_brand_name) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();

		Elements product = document.select("div[class=product-detail]");
		String s_title = product.select("h1[class=page-title]").text();

		String s_sku = product.select("div[class=product attribute sku]").text();
		String s_instock = "Available";
		
		String s_description = product.select("div[class=content]").html();
		String s_disclaimer = "";
		String s_delivery = "";

		String[] price = product.select("span[id^=product-price-]").text().replace("PKR", "").replace(",", "").split("\\s");
		String s_org_price="";

		String s_dis_price =  price[0];
		System.out.println(s_dis_price);
		Elements div = document.select("div[class^=MagicToolboxContainer selectorsRight ]").select("a");

		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			imgDirectoy.put("s_pic_" + i++, e.attr("href"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");

		String s_pic_2 = (String) imgDirectoy.get("s_pic_2");
		String s_pic_3 = (String) imgDirectoy.get("s_pic_3");
		String s_pic_4 = "";// (String) imgDirectoy.get("s_pic_4");
		// System.out.println(s_pic_3);
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
		preparedStmt.setString(15, "Women");
		preparedStmt.execute();
		
		String sqlUpdate = "update links set status =1 where id=?";
		PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		preparedUpdateStmt.setInt(1, id);
		preparedUpdateStmt.executeUpdate();
	}

}
