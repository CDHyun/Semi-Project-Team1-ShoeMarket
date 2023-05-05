package com.javalec.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.javalec.dto.CartDto;
import com.javalec.util.ShareVar;

public class CartDao {

	private final String url_mysql = ShareVar.DBName;
	private final String id_mysql = ShareVar.DBUser;
	private final String pw_mysql = ShareVar.DBPass;
	
	
	int cartNO;
	int cartQty;
	int cartPirce;
	String name;
	String fileName;
	FileInputStream file;
	
	public CartDao() {
		// TODO Auto-generated constructor stub
	}
	

	public CartDao(String name) {
		super();
		this.name = name;
	}


	public ArrayList<CartDto> selectList(){
		ArrayList<CartDto> beanList = new ArrayList<CartDto>();	//데이터를 쌓을 장소
		
		String query = "select cartNO, productName, productPrice, cartQty from cart, user, product";
		String query1 = " where cart.userid = user.userid and cart.productCode = product.productCode";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");											
			Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);	
			Statement stmt_mysql = conn_mysql.createStatement();
			
			ResultSet rs = stmt_mysql.executeQuery(query + query1);	// 데이터 베이스(데이터 한줄이 들어가있다)
			
			while(rs.next()) {							// rs 에서 읽어올 것이 없을 경우까지
				int wkSeq = rs.getInt(1);				// 한줄에 데이터 하나씩 빼주는 작업
				String wkName = rs.getString(2);
				int wqPirce = rs.getInt(3);
				int wkQty = rs.getInt(4);
				
//				ShareVar.filename += 1;
//				File file = new File(Integer.toString(ShareVar.filename));
//				FileOutputStream output = new FileOutputStream(file);
//				InputStream input = rs.getBinaryStream(5);
//				byte[] buffer = new byte[1024];
//				while(input.read(buffer) > 0) {
//					output.write(buffer);
//				}
				
				CartDto dto = new CartDto(wkSeq, wkName, wkQty, wqPirce); // db에 가져온 데이터를 한줄에 넣기위해
				beanList.add(dto);
			}
			
			
		}catch (Exception e) {	// 에러 걸리면
			e.printStackTrace();
		}
		
		return beanList;	
		
	}
	
	
	public boolean deleteAction() {
		PreparedStatement ps = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt_mysql = conn_mysql.createStatement();
			
			String query = "delete from userinfo where userid = ?";
			
			ps = conn_mysql.prepareStatement(query);
			ps.setString(1, name);
			
			
			ps.executeUpdate();
			conn_mysql.close();
						
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}// end
