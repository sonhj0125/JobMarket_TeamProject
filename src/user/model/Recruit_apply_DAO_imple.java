package user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import common.MYDBConnection;
import company.domain.Company_DTO;
import company.domain.Company_type_DTO;
import user.domain.Paper_DTO;
import user.domain.Recruit_Apply_DTO;
import user.domain.User_DTO;

public class Recruit_apply_DAO_imple implements Recruit_apply_DAO {
	
	private Connection conn = MYDBConnection.getConn();      // 데이터베이스 서버 연결
   private PreparedStatement pstmt;   // 우편배달부
   private ResultSet rs;
   
   // ◆◆◆ === 자원반납을 해주는 메소드 === ◆◆◆ //
   private void close() {
      try {
         if(rs != null) {
            rs.close();
            rs = null;
         }
         if(pstmt != null) {
            pstmt.close();
            pstmt = null;
         }
      } catch(SQLException e) {
         e.printStackTrace();
      }   // end of try~catch----------
   }   // end of private void close()---------------
	
	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆  
	
	

   
   // ◆◆◆ === 구인회사 조회 === ◆◆◆ //
   @Override
    public Company_DTO company_search(Map<String, String> paraMap) {
      
      Company_DTO cdto = null;
      try {
         String sql = " select A.company_name"
         		  + "		 , to_char(B.begin_day,'yyyy-mm-dd') as begin_day"
         		  + "		 , A.ceo_name, A.company_address "
                  + "        , B.employee_num "
                  + "        , decode(B.public_status, 0, '비상장', 1, '상장') AS public_status "
                  + "        , B.capital_money, B.companylist_num "
                  + " from tbl_company A LEFT JOIN tbl_company_type B "
                  + " ON A.company_id = B.fk_company_id "
                  + " where lower(A.company_name) = lower(?) ";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, paraMap.get("company_name"));
         
         rs = pstmt.executeQuery(); // SQL문 실행
         
         if(rs.next()) {
            cdto = new Company_DTO();
            
            cdto.setCompany_name(rs.getString("company_name"));
            cdto.setCeo_name(rs.getString("ceo_name"));
            cdto.setCompany_address(rs.getString("company_address"));
            
            Company_type_DTO ctdto = new Company_type_DTO();
            ctdto.setBegin_day(rs.getString("begin_day"));
            ctdto.setEmployee_num(rs.getString("employee_num"));
            ctdto.setPublic_status(rs.getString("public_status"));
            ctdto.setCapital_money(rs.getString("capital_money"));
            ctdto.setCompanylist_num(rs.getString("companylist_num"));
            
            cdto.setCompany_type_detail(ctdto);
         }   // end of if--------------------  
      } catch (SQLException e) {
            e.printStackTrace(); 
      } finally {
         close();
      }      // end of try~catch~finally------------------
      return cdto;
   }   // end of public Company_DTO company_search(Map<String, String> paraMap)-----



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
   
   
   
   // ◆◆◆ === 채용공고에 채용지원서 넣기 === ◆◆◆ //
   @Override
   public int my_recruit_apply(Map<String, String> paraMap, int paper_code) {
	   int result = 0;
	      
	      try {
	         
	         String sql = " insert into tbl_recruit_apply(fk_recruit_no, fk_paper_code, apply_motive) "
	                  + " values(?, ?, ?) ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("recruit_no"));
	         pstmt.setInt(2, paper_code);
	         pstmt.setString(3, paraMap.get("apply_motive"));
	         
	         result = pstmt.executeUpdate();
	         
	      } catch (SQLException e) {
	    	  e.printStackTrace();
	      } finally {
	         close(); 
	      }		// end of try~catch~finally-----------------------
	      return result;
   }	// end of public int my_recruit_apply(Recruit_Apply_DTO radto)-----------
   
   
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆

	// ◆◆◆ === 지원한 공고 조회 === ◆◆◆ //
	@Override
	public List<User_DTO> applylist(Recruit_apply_DAO radao, User_DTO user) {
		
		List<User_DTO> applyList = new ArrayList<>();
		
		try {
			String sql = " with "
					+ "   A as "	// 학력
					+ "   ( "
					+ "   select academy_name, user_id "
					+ "   from tbl_academy A join tbl_user_info U "
					+ "   on A.academy_code = U.fk_academy_code "
					+ "   ) "
					+ "   , R as "	// 취업우대
					+ "   ( "
					+ "   select priority_name, user_id "
					+ "   from tbl_priority P join tbl_user_info U "
					+ "   on P.priority_code = U.fk_priority_code "
					+ "   ) "
					+ "   , "
					+ "   U as "
					+ "   ( "		// 이력서
					+ "   select career, paper_name, user_id, fk_license_code, paper_code "
					+ "   from tbl_user_info U join tbl_paper P "
					+ "   on U.user_id = P.fk_user_id "
					+ "   ) "
					+ "   , "
					+ "   D as "
					+ "   ( "		// 자격증
					+ "   select license_name, fk_license_code "
					+ "   from tbl_license_detail join tbl_paper "
					+ "   on license_code = fk_license_code "
					+ "   ) "
					+ "   , Q as "
					+ "   ( "		// 지원
					+ "   select paper_code , fk_recruit_no, apply_motive , apply_day "
					+ "   from tbl_recruit_apply N join tbl_paper G "
					+ "   on N.fk_paper_code = G.paper_code "
					+ "   )  "		// select 문 시작
					+ "   select  Q.fk_recruit_no , U.career ,Q.apply_motive, Q.paper_code , U.paper_name "
					+ "    , A.academy_name , R.priority_name , nvl(license_name, ' ') as 취업우대 "
					+ "    , Q.apply_day "
					+ "   from A join R "
					+ "   on A.user_id = R.user_id join U "
					+ "   on R.user_id = U.user_id left join D "
					+ "   on U.fk_license_code = D.fk_license_code join Q "
					+ "   on U.paper_code = Q.paper_code "
					+ "   where U.user_id = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, user.getUser_id());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Recruit_Apply_DTO rdto = new Recruit_Apply_DTO();
				rdto.setRecruit_no(rs.getString("fk_recruit_no"));
				rdto.setApply_motive(rs.getString("apply_motive"));
				rdto.setPaper_code(rs.getInt("paper_code"));
				rdto.setApply_day(rs.getString("apply_day"));
				
				Paper_DTO pdto = new Paper_DTO();
				pdto.setCareer(rs.getString("career"));
				pdto.setPaper_name(rs.getString("paper_name"));
				
				User_DTO udto = new User_DTO();
				udto.setPriority_name(rs.getString("취업우대"));
				udto.setAcademy_name(rs.getString("academy_name"));
				
				udto.setRcapply(rdto);
				
				udto.setPaper(pdto);
				
				System.out.println(udto);
				applyList.add(udto);	
			}	// end of while(rs.next())-------
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}	// end of try~catch~finally-----------
		return applyList;
		
	}	// end of public List<Recruit_Apply_DTO> applylist(Recruit_apply_DAO radao, User_DTO user)-----

	

	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	
	
	// ◆◆◆ === 합격여부 조회 === ◆◆◆ //
	@Override
	public int check_success(Scanner sc, User_DTO user, String num) {

		int result = 0;
		
		try {
	         String sql = "with "
	         		+ "   U as "
	         		+ "   (  "
	         		+ "   select user_id, paper_code "
	         		+ "   from tbl_user_info U join tbl_paper P  "
	         		+ "   on U.user_id = P.fk_user_id  "
	         		+ "   ) "
	         		+ "   , Q as "
	         		+ "   (  "
	         		+ "   select fk_recruit_no, fk_paper_code , success_status "
	         		+ "   from tbl_recruit_apply N join tbl_paper G "
	         		+ "   on N.fk_paper_code = G.paper_code "
	         		+ "   ) "
	         		+ "   select Q.success_status "
	         		+ "   from U join Q "
	         		+ "   on U.paper_code = Q.fk_paper_code "
	         		+ "   where U.user_id = ? and Q.fk_recruit_no = ? ";
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         pstmt.setString(1, user.getUser_id());
	         pstmt.setString(2, num);
	         
	         rs = pstmt.executeQuery(); // SQL문 실행
	         
	         rs.next();
	         
	         result= rs.getInt("success_status");	 
	           
	      } catch (SQLException e) {
	            e.printStackTrace(); 
	      } finally {
	         close();
	      }      // end of try~catch~finally------------------
	      return result;
		
	}	// end of public int check_success(Scanner sc, User_DTO user)------------------


	


   
   
   
   
   
   
   
   
   
   


   
   





	






	

	

}
