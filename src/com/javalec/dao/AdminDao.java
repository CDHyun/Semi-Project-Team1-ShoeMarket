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

import javax.swing.ImageIcon;

import com.javalec.dto.AdminDto;
import com.javalec.util.ShareVar;

public class AdminDao {
	private final String url_mysql = ShareVar.DBName;
	private final String id_mysql = ShareVar.DBUser;
	private final String pw_mysql = ShareVar.DBPass;

	int brandNo;
	int productCode;
	String brandName;
	String productName;
	int price;
	int size;
	int stock;
	String productImageName;
	FileInputStream productImage;

	String conditionQueryColumn;
	String tfSearch;

	public AdminDao() {
		// TODO Auto-generated constructor stub
	}

	// tableInit() 생성 후 테이블에 들어갈 데이터 생성자 ((순서), 브랜드 이름, 제품명, 사이즈, 재고)
	public AdminDao(String brandName, String productName, int size, int stock) {
		super();
		this.brandName = brandName;
		this.productName = productName;
		this.size = size;
		this.stock = stock;
	}

	// 조건 검색을 위해 콤보박스에서 선택할 데이터 생성자
	public AdminDao(String conditionQueryColumn, String tfSearch) {
		super();
		this.conditionQueryColumn = conditionQueryColumn;
		this.tfSearch = tfSearch;
	}

	// 데이터 입력을 위해 메인에서 받아올 데이터 생성자
	public AdminDao(int brandNo, String brandName, int productCode, String productName, int price, int size,
			int stock) {
		super();
		this.brandNo = brandNo;
		this.brandName = brandName;
		this.productCode = productCode;
		this.productName = productName;
		this.price = price;
		this.size = size;
		this.stock = stock;
	}

	/* 상품 추가 해주는 생성자 */
	public AdminDao(int brandNo, String productName, int price, int size, int stock, String productImageName,
			FileInputStream productImage) {
		super();
		this.brandNo = brandNo;
		this.productName = productName;
		this.price = price;
		this.size = size;
		this.stock = stock;
		this.productImageName = productImageName;
		this.productImage = productImage;
	}
	/* 수정을 위한 생성자 */
	public AdminDao(int brandNo, int productCode, String productName, int price, int size, int stock,
			String productImageName, FileInputStream productImage) {
		super();
		this.brandNo = brandNo;
		this.productCode = productCode;
		this.productName = productName;
		this.price = price;
		this.size = size;
		this.stock = stock;
		this.productImageName = productImageName;
		this.productImage = productImage;
	}
	
	/* 재고 관리를 해주는 생성자 */
	public AdminDao(int productCode, int size, int stock) {
		super();
		this.productCode = productCode;
		this.size = size;
		this.stock = stock;
	}

	/* 삭제를 위한 제품 코드를 가져와주는 생성자 */
	public AdminDao(int productCode, int size) {
		super();
		this.productCode = productCode;
		this.size = size;
	}

	// 윈도우 창 오픈 초기 테이블 데이터 table에 불러오기 - 브랜드명, 제품명, 사이즈, 재고량
	public ArrayList<AdminDto> selectList() {
		ArrayList<AdminDto> dtoList = new ArrayList<AdminDto>();

		String query = "SELECT p.productCode, b.brandName, po.size, p.productName, po.productStock" + " FROM brand b"
				+ " JOIN product p ON b.brandNo = p.brandNo"
				+ " LEFT JOIN productOption po ON po.productCode = p.productCode;";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt_mysql = conn_mysql.createStatement();

			ResultSet rs = stmt_mysql.executeQuery(query);

			while (rs.next()) {
				int wkCode = rs.getInt(1);
				String brandName = rs.getString(2);
				int size = rs.getInt(3);
				String productName = rs.getString(4);
				int stock = rs.getInt(5);

				AdminDto dto = new AdminDto(brandName, productName, size, stock, wkCode);
				dtoList.add(dto);
			}

			conn_mysql.close();

		} catch (Exception e) {
//			e.printStackTrace();
		}

		return dtoList;
	}

	// 상세 정보 텍스트 필드를 채우는 메소드
	public ArrayList<AdminDto> queryAction(int size, int productCode) {
		PreparedStatement ps = null;
		ArrayList<AdminDto> beanList = new ArrayList<AdminDto>();
		String query = "SELECT b.brandNo, b.brandName, p.productCode, p.productName, po.size, p.productPrice, po.productStock, p.productInsertdate, p.productImageName, p.productImage"
				+ " FROM brand b, product p, productOption po"
				+ " WHERE b.brandNo = p.brandNo AND po.productCode = p.productCode";

		if (size != 0) {
			query += " AND po.size = " + size;
		} else {
			query += " AND (po.size IS NULL OR po.size = 0)";
		}

		query += " AND p.productCode = " + productCode;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt_mysql = conn_mysql.createStatement();

			ResultSet rs = stmt_mysql.executeQuery(query);
			while (rs.next()) {
				int wkBrandNo = rs.getInt(1);
				String wkBrandName = rs.getString(2);
				int wkProductCode = rs.getInt(3);
				String wkProductName = rs.getString(4);
				int wkSize = rs.getInt(5);
				int wkPrice = rs.getInt(6);
				int wkStock = rs.getInt(7);
				String wkDate = rs.getString(8);
				String wkImageName = rs.getString(9);
				File file = new File("./" + wkImageName);
				FileOutputStream output = new FileOutputStream(file);
				InputStream input = rs.getBinaryStream(10);
				byte[] buffer = new byte[1024];
				AdminDto adminDto = new AdminDto(wkBrandNo, wkBrandName, wkProductName, wkSize, wkProductCode, wkPrice,
						wkStock, wkDate, wkImageName);
				beanList.add(adminDto);
				while (input.read(buffer) > 0) {
					output.write(buffer);
				}

			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return beanList;

	}

	// 사용자가 입력한 조건 검색
	public ArrayList<AdminDto> conditionList(String conditionQueryColumn, String tfSearch) {
		ArrayList<AdminDto> beanList = new ArrayList<AdminDto>();

		String whereDefault = "select p.productCode, b.brandName, p.productName, po.size, po.productStock";
		String whereDefault1 = " from brand b, product p, productOption po";
		String whereDefault2 = " where b.brandNo = p.brandNo and po.productCode = p.productCode and "
				+ conditionQueryColumn + " like " + "'%" + tfSearch + "%'";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt_mysql = conn_mysql.createStatement();

			ResultSet rs = stmt_mysql.executeQuery(whereDefault + whereDefault1 + whereDefault2);

			while (rs.next()) {
				int wkCode = rs.getInt(1);
				String brandName = rs.getString(2);
				String productName = rs.getString(3);
				int size = rs.getInt(4);
				int stock = rs.getInt(5);

				AdminDto dto = new AdminDto(brandName, productName, size, stock, wkCode);
				beanList.add(dto);
			}

			conn_mysql.close();

		} catch (Exception e) {
//			e.printStackTrace();
		}

		return beanList;
	}

	// 데이터 입력하기 <<<< 이미지!!!!
	public boolean insertAction() {
		PreparedStatement ps = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt = con.createStatement();

//			AdminDao adminDao = new AdminDao(brandNo, productName, productPrice, productImageName, input);

			String query1 = "INSERT INTO product(brandNo, productName, productPrice, productImageName, productImage) VALUES (?, ?, ?, ?, ?)";
			String query2 = "INSERT INTO productOption(productCode, size, productStock) VALUES (LAST_INSERT_ID(), ?, ?)";

			PreparedStatement ps1 = con.prepareStatement(query1);
			ps1.setInt(1, brandNo);
			ps1.setString(2, productName);
			ps1.setInt(3, price);
			ps1.setString(4, productImageName);
			ps1.setBlob(5, productImage);

			ps1.executeUpdate();

			PreparedStatement ps2 = con.prepareStatement(query2);
			ps2.setInt(1, size);
			ps2.setInt(2, stock);

			ps2.executeUpdate();

			con.close();

		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
		return true;

	}

	// 데이터 수정하기
	public int updateAction() {
		int rowsAffected1 = 0;
		int rowsAffected2 = 0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt = con.createStatement();
			PreparedStatement ps_product = null;
			PreparedStatement ps_productOption = null;
			
			String updateProductQuery = "UPDATE product SET brandNo = ?, productName = ?, productPrice = ?, productImageName = ?, productImage = ? WHERE productCode = ?";
			ps_product = con.prepareStatement(updateProductQuery);
			ps_product.setInt(1, brandNo);
			ps_product.setString(2, productName);
			ps_product.setInt(3, price);
			ps_product.setString(4, productImageName);
			ps_product.setBinaryStream(5, productImage);
			ps_product.setInt(6, productCode);
			rowsAffected1 += ps_product.executeUpdate();
			System.out.println("rowsAffected1 : " + rowsAffected1);

			String updateProductOptionQuery = "UPDATE productOption SET productStock = ? WHERE productCode = ? and size = ?";
			ps_productOption = con.prepareStatement(updateProductOptionQuery);
			ps_productOption.setInt(1, stock);
			ps_productOption.setInt(2, productCode);
			ps_productOption.setInt(3, size);
			rowsAffected2 += ps_productOption.executeUpdate();
			System.out.println("rowsAffected2 : " + rowsAffected2);
			if(rowsAffected1 > 0 || rowsAffected2 > 0) {
				return 1;
			}
			con.close();
		} catch (Exception e) {
//			e.printStackTrace();
			return 0;
		}
		return 1;

	}

	public boolean deleteAction() {
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		String query1 = "delete from productOption where productCode = ? and size = ?";
		String query2 = "delete from product where productCode = ?";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt = con.createStatement();

			ps1 = con.prepareStatement(query1);
			ps1.setInt(1, productCode);
			ps1.setInt(2, size);
			ps1.executeUpdate();

			ps2 = con.prepareStatement(query2);
			ps2.setInt(1, productCode);
			ps2.executeUpdate();

			int rowsAffected = ps1.executeUpdate();
			int rowsAffected2 = ps2.executeUpdate();
			if (rowsAffected > 0 && rowsAffected2 > 0) {
				return true;
			}
			con.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return false;
	}

	public int addStock() {
		int addStockCount = 0;
		PreparedStatement ps = null;
		String query = "insert into productOption(size, productCode, productStock) values(?, ?, ?)";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
			Statement stmt = con.createStatement();
			
			ps = con.prepareStatement(query);
			ps.setInt(1, size);
			ps.setInt(2, productCode);
			ps.setInt(3, stock);
			
			addStockCount += ps.executeUpdate();
			con.close();
		}catch (Exception e) {
//			e.printStackTrace();
			return 0;
		}
		return addStockCount;
		
		
		
	}
	
	
	
	
	
	
	
	
}	// End Class
