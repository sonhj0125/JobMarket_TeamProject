package company.model;

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
import company.domain.Recruit_INFO_DTO;
import user.domain.User_DTO;

public class Recruit_DAO_imple implements Recruit_DAO {

	// field

	private Connection conn = MYDBConnection.getConn();		// 데이터베이스 서버 연결
	private PreparedStatement pstmt;	// 우편배달부
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
		}	// end of try~catch----------
	}	// end of private void close()---------------
	
	

// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	
	// ◆◆◆ === 채용공고 등록 === ◆◆◆ //
	@Override
	public int recruit_write(Map<String, String> paraMap, Company_DTO company) {
		int result = 0;
		String hiretype_name = "";
	      
		try {
	         String selectsql = " select hiretype_name "
	                      + " from tbl_hiretype "
	                      + " where hiretype_code = ? ";
	         
	         pstmt = conn.prepareStatement(selectsql);
	         
	         pstmt.setString(1, paraMap.get("fk_hiretype_code"));
	         
	         rs = pstmt.executeQuery();
	         
	         if(rs.next()) {
	            hiretype_name = rs.getString("hiretype_name");
	         }
	         
	         String insertsql = " insert into tbl_recruit_info (recruit_no, fk_company_id , manager_name , manager_email , "            
                  + "  recruit_title, recruit_people , recruit_deadline, year_salary, recruit_content , "
                  + "  work_day , work_time, career , fk_hiretype_code, recruit_field ) "
                  + "  values(to_char(sysdate, 'yyyymmdd') || '-' || SEQ_RECRUIT_NO.nextval , ? , ? , ? , "
                  + "   ?, ? , ?  ,? , ? , ? , ? , ? , ? , ?)  ";      
            
	         pstmt = conn.prepareStatement(insertsql);         
            
	         pstmt.setString(1, paraMap.get("fk_company_id") );   
	         pstmt.setString(2, paraMap.get("manager_name") ); 
	         pstmt.setString(3, paraMap.get("manager_email") );   
	         pstmt.setString(4, paraMap.get("recruit_title") );
	         pstmt.setString(5, paraMap.get("recruit_people") );
	         pstmt.setString(6, paraMap.get("recruit_deadline"));
	         pstmt.setString(7, paraMap.get("year_salary") );
	         pstmt.setString(8, paraMap.get("recruit_content") );
	         pstmt.setString(9, paraMap.get("work_day") );
	         pstmt.setString(10, paraMap.get("work_time") );
	         pstmt.setString(11, paraMap.get("career") );
	         pstmt.setString(12, paraMap.get("fk_hiretype_code") );
	         pstmt.setString(13, hiretype_name );
            
	         result = pstmt.executeUpdate();      // SQL문 실행
	            
         } catch (SQLException e) {
    		 e.printStackTrace();
         } finally {
        	 close();
         }   // end try~catch~finally--------------------
	         return result;
	   } // end of public int recruit_write(Map<String, String> paraMap, Company_DTO company)
	
	
	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆

	
	
	// ◆◆◆ === 채용공고 조회(전체) 리스트 === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> recruit_info_list() {
		List<Recruit_INFO_DTO> recruit_info_list = new ArrayList<>();
	      
	      try {
	    	  String sql = " select B.recruit_no "
	                  + "      , CASE WHEN length(B.recruit_title) > 10 THEN substr(B.recruit_title, 1, 7) || '...' ELSE B.recruit_title END AS recruit_title "
	                  + "      , A.company_name, A.company_address, B.career, B.year_salary "
	                  + "      , '~' || B.recruit_deadline AS recruit_deadline "
	                  + " from TBL_COMPANY A RIGHT JOIN TBL_RECRUIT_INFO B "
	                  + " ON A.company_id = B.fk_company_id ";
	            
	            pstmt = conn.prepareStatement(sql);
	            
	            rs = pstmt.executeQuery(); // SQL문 실행
	            
	            while(rs.next()) {
	               
	               Recruit_INFO_DTO rdto = new Recruit_INFO_DTO();
	               
	               rdto.setRecruit_no(rs.getString("recruit_no"));
	               rdto.setRecruit_title(rs.getString("recruit_title"));
	               rdto.setCareer(rs.getString("career"));
	               rdto.setYear_salary(rs.getString("year_salary"));
	               rdto.setRecruit_deadline(rs.getString("recruit_deadline"));
	              
	               Company_DTO cdto = new Company_DTO();
	               cdto.setCompany_name(rs.getString("company_name"));
	               cdto.setCompany_address(rs.getString("company_address"));
	           
	               rdto.setCdto(cdto);
	               
	               recruit_info_list.add(rdto);
	               
	            }   // end of while--------------------  
	      } catch (SQLException e) {
               	e.printStackTrace(); 
	      } finally {
	    	  close();
	      }      // end of try~catch~finally------------------
	      return recruit_info_list;
	}	// end of public List<Recruit_INFO_DTO> recruit_info_list()------



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	

	// ◆◆◆ === 채용공고 조회 === ◆◆◆ //
	 @Override
	 public Recruit_INFO_DTO recruit_info(Map<String, String> paraMap) {
	      
		 Recruit_INFO_DTO rdto = null;
	         
		 try {
	           
			 String sql = " select B.recruit_no, A.company_name, B.recruit_title, B.career, B.recruit_field, B.year_salary, B.work_day, B.work_time "
	                  + "      , B.recruit_content, B.recruit_people, B.manager_name, B.manager_email, to_char(B.recruit_registerday, 'yy/mm/dd') AS recruit_registerday, B.recruit_deadline "
	                  + " from tbl_company A JOIN tbl_recruit_info B "
	                  + " ON A.company_id = B.fk_company_id "
	                  + " where B.recruit_no = ? ";
	           
	         pstmt = conn.prepareStatement(sql);
	      
	         pstmt.setString(1, paraMap.get("recruit_no"));
	           
	           rs = pstmt.executeQuery(); // SQL문 실행
	           
	           if(rs.next()) {   
	              rdto = new Recruit_INFO_DTO();   
	              
	              rdto.setRecruit_no(rs.getString("recruit_no"));
	              rdto.setRecruit_title(rs.getString("recruit_title"));
	              rdto.setCareer(rs.getString("career"));
	              rdto.setRecruit_field(rs.getString("recruit_field"));
	              rdto.setYear_salary(rs.getString("year_salary"));
	              rdto.setWork_day(rs.getString("work_day"));
	              rdto.setWork_time(rs.getString("work_time"));
	              rdto.setRecruit_content(rs.getString("recruit_content"));
	              rdto.setRecruit_people(rs.getString("recruit_people"));
	              rdto.setManager_name(rs.getString("manager_name"));
	              rdto.setManager_email(rs.getString("manager_email"));
	              rdto.setRecruit_registerday(rs.getString("recruit_registerday"));
	              rdto.setRecruit_deadline(rs.getString("recruit_deadline"));
	              
	              Company_DTO cdto = new Company_DTO();
	               cdto.setCompany_name(rs.getString("company_name"));
	              
	               rdto.setCdto(cdto);
	           }
		 } catch (SQLException e) {
			 e.printStackTrace();
		 } finally {
			 close();
		 }
		 return rdto;
   }   // end of public Recruit_INFO_DTO recruit_info(Map<String, String> paraMap)------
		
		
	 
	 
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
		
		
	
	// ◆◆◆ === 채용공고 조회(로그인한 기업의 공고만) === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> All_recruit(Company_DTO company, Recruit_INFO_DTO ridto_register) {
		Recruit_INFO_DTO recruit_view = new Recruit_INFO_DTO();
		
		List<Recruit_INFO_DTO> recruitList = new ArrayList<>();
		
		try {
			String sql = " select recruit_no AS 공고번호 "
				         + " , recruit_field AS 채용분야 "
				         + " , recruit_title AS 공고명 "
				         + " , recruit_content AS 공고내용 "
				         + " , recruit_registerday AS 등록일 "
				         + " , recruit_deadline AS 마감일 "
				         + " , career AS 신입경력여부 "
				         + " , year_salary AS 연봉 "
				         + " , recruit_people AS 채용인원 "
				         + " , work_day AS 근무요일 "
				         + " , work_time AS 근무시간 "
				         + " , manager_email AS 담당자이메일 "
				         + " , manager_name AS 담당자명 "
				         + " from tbl_recruit_info "
				         + " where fk_company_id = ? ";
							
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, company.getCompany_id());  
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {	
				recruit_view = new Recruit_INFO_DTO();
				
				recruit_view.setRecruit_no(rs.getString("공고번호"));
				recruit_view.setRecruit_field(rs.getString("채용분야"));
				recruit_view.setRecruit_title(rs.getString("공고명"));
				recruit_view.setRecruit_content(rs.getString("공고내용"));
				recruit_view.setRecruit_registerday(rs.getString("등록일"));
				recruit_view.setRecruit_deadline(rs.getString("마감일"));
				recruit_view.setCareer(rs.getString("신입경력여부"));
				recruit_view.setYear_salary(rs.getString("연봉"));
				recruit_view.setRecruit_people(rs.getString("채용인원"));
				recruit_view.setWork_day(rs.getString("근무요일"));
				recruit_view.setWork_time(rs.getString("근무시간"));
				recruit_view.setManager_email(rs.getString("담당자이메일"));
				recruit_view.setManager_name(rs.getString("담당자명"));
				
				recruitList.add(recruit_view);
			} // end of while(rs.next())--------
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		} // end of finally--------------
		return recruitList;
	}	// end of public List<Recruit_INFO_DTO> All_recruit(Recruit_INFO_DTO ridto_register)--------



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆


	
	// ◆◆◆ === 로그인한 기업의 채용공고 갯수 가져오기  === ◆◆◆ //
	@Override
	 public int get_recruitlist_count(String company_id) {
	      
	      int result = 0;
	      
	      try {
	            // conn = MyDBConnection.getConn(); 데이터베이스 연결하기
	          
	         String sql = " select count(*) "
	               + " from tbl_recruit_info "
	               + " where fk_company_id = ? "
	               + " group by fk_company_id" ;  
	          
	            pstmt = conn.prepareStatement(sql); // 우편배달부 
	            
	            pstmt.setString(1, company_id);
	            
	            rs = pstmt.executeQuery(); // SQL문 실행  
	            
	            if (rs.next()) {
	             result = rs.getInt(1);
	             }
	        } catch (SQLException e) {
	              e.printStackTrace();
	        } finally { 
	           close();   // 자원 반납
	        } // end of finally
   	      return result;
	   } // end of public void get_recruitlist_count(String company_id)

			
			
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
			
	
	
	// ◆◆◆ === 분야별공고검색 - 1. 업종별 검색 === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> search_job_type(String job_type_select, Company_DTO company) {
		
		Recruit_INFO_DTO search_job_type = new Recruit_INFO_DTO();
		
		List<Recruit_INFO_DTO> search_jtype = null;
		
		try {
			
			String sql = " with "
						+" v as "
						+" ( "
						+" select * "
						+" from tbl_company C join tbl_jobtype J "
						+" on C.fk_jobtype_code = J.jobtype_code " 
						+" ) "
						+" select R.recruit_no, R.recruit_field , R.recruit_title " 
						+"      , R.recruit_content, R.recruit_registerday , R.recruit_deadline " 
						+"      , R.career , R.year_salary , R.recruit_people " 
						+"      , R.work_day , R.work_time , R.manager_email , R.manager_name "  
						+" from tbl_recruit_info R join v "
						+" on R.fk_company_id = v.company_id "
						+" where v.jobtype_name like ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, "%"+ job_type_select + "%");
			
			rs = pstmt.executeQuery();
			
			search_jtype = new ArrayList<>();
			
			while(rs.next()) {	
				search_job_type = new Recruit_INFO_DTO();
				
				search_job_type.setRecruit_no(rs.getString("recruit_no"));
				search_job_type.setRecruit_field(rs.getString("recruit_field"));
				search_job_type.setRecruit_title(rs.getString("recruit_title"));
				search_job_type.setRecruit_content(rs.getString("recruit_content"));
				search_job_type.setRecruit_registerday(rs.getString("recruit_registerday"));
				search_job_type.setRecruit_deadline(rs.getString("recruit_deadline"));
				search_job_type.setCareer(rs.getString("career"));
				search_job_type.setYear_salary(rs.getString("year_salary"));
				search_job_type.setRecruit_people(rs.getString("recruit_people"));
				search_job_type.setWork_day(rs.getString("work_day"));
				search_job_type.setWork_time(rs.getString("work_time"));
				search_job_type.setManager_email(rs.getString("manager_email"));
				search_job_type.setManager_name(rs.getString("manager_name"));
				
				search_jtype.add(search_job_type);
				
			} // end of while(rs.next())
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		} // end of finally
		return search_jtype;
	} // end of public List<Recruit_INFO_DTO> search_job_type(String job_type_select, Company_DTO company)

		
		
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
		
		
	// ◆◆◆ === 분야별공고검색 - 2. 지역별 검색 === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> search_local(String local_select, Company_DTO company) {
		
		Recruit_INFO_DTO search_local = new Recruit_INFO_DTO();
		
		List<Recruit_INFO_DTO> search_ltype = null;
		
		try {
			
			String sql = " with "
						+" v as "
						+" ( "
						+" select * "
						+" from tbl_company C join tbl_local L "
						+" on C.fk_local_code = L.local_code  "
						+" ) "
						+" select R.recruit_no, R.recruit_field , R.recruit_title "
						+"      , R.recruit_content, R.recruit_registerday , R.recruit_deadline " 
						+"      , R.career , R.year_salary , R.recruit_people " 
						+"      , R.work_day , R.work_time , R.manager_email , R.manager_name "  
						+" from tbl_recruit_info R join v " 
						+" on R.fk_company_id = v.company_id "
						+" where v.local_name like ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, "%"+ local_select + "%");
			
			rs = pstmt.executeQuery();
			
			search_ltype = new ArrayList<>();
			
			while(rs.next()) {	
				search_local = new Recruit_INFO_DTO();
				
				search_local.setRecruit_no(rs.getString("recruit_no"));
				search_local.setRecruit_field(rs.getString("recruit_field"));
				search_local.setRecruit_title(rs.getString("recruit_title"));
				search_local.setRecruit_content(rs.getString("recruit_content"));
				search_local.setRecruit_registerday(rs.getString("recruit_registerday"));
				search_local.setRecruit_deadline(rs.getString("recruit_deadline"));
				search_local.setCareer(rs.getString("career"));
				search_local.setYear_salary(rs.getString("year_salary"));
				search_local.setRecruit_people(rs.getString("recruit_people"));
				search_local.setWork_day(rs.getString("work_day"));
				search_local.setWork_time(rs.getString("work_time"));
				search_local.setManager_email(rs.getString("manager_email"));
				search_local.setManager_name(rs.getString("manager_name"));
				
				search_ltype.add(search_local);
				
			} // end of while(rs.next())
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		} // end of finally
		return search_ltype;
	} // end of public List<Recruit_INFO_DTO> search_local(String local_select, Company_DTO company)



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆			
			
			
			
	// ◆◆◆ === 분야별공고검색 - 3. 경력별 검색 === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> search_career(String career_select, Company_DTO company) {
		
		Recruit_INFO_DTO search_career = new Recruit_INFO_DTO();
		
		List<Recruit_INFO_DTO> search_cafield = null;
		
		try {
			
			String sql = " select recruit_no, recruit_field , recruit_title " 
						+" , recruit_content, recruit_registerday , recruit_deadline "
						+" , career , year_salary , recruit_people " 
						+" , work_day , work_time , manager_email , manager_name " 
						+" from tbl_recruit_info "
						+" where career like ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, "%"+ career_select + "%");
			
			rs = pstmt.executeQuery();
			
			search_cafield = new ArrayList<>();
			
			while(rs.next()) {	
				search_career = new Recruit_INFO_DTO();
				
				search_career.setRecruit_no(rs.getString("recruit_no"));
				search_career.setRecruit_field(rs.getString("recruit_field"));
				search_career.setRecruit_title(rs.getString("recruit_title"));
				search_career.setRecruit_content(rs.getString("recruit_content"));
				search_career.setRecruit_registerday(rs.getString("recruit_registerday"));
				search_career.setRecruit_deadline(rs.getString("recruit_deadline"));
				search_career.setCareer(rs.getString("career"));
				search_career.setYear_salary(rs.getString("year_salary"));
				search_career.setRecruit_people(rs.getString("recruit_people"));
				search_career.setWork_day(rs.getString("work_day"));
				search_career.setWork_time(rs.getString("work_time"));
				search_career.setManager_email(rs.getString("manager_email"));
				search_career.setManager_name(rs.getString("manager_name"));
				
				search_cafield.add(search_career);
				
			} // end of while(rs.next())
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		} // end of finally
		return search_cafield;
	}	// end of public List<Recruit_INFO_DTO> search_career(String career_select, Company_DTO company)-----


// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	
	// ◆◆◆ === 분야별공고검색 - 4. 채용분야 검색 === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> search_recruit_field(String recruit_type_select , Company_DTO company) {
		
		Recruit_INFO_DTO search_recruit_field = new Recruit_INFO_DTO();
		
		List<Recruit_INFO_DTO> search_refield = null;
		
		try {
			
			String sql = " SELECT A.recruit_no, A.recruit_field, A.recruit_title "
					+" , A.recruit_content, A.recruit_registerday, A.recruit_deadline "
					+" , A.career, A.year_salary, A.recruit_people "
					+" , A.work_day, A.work_time, A.manager_email, A.manager_name "
					+" from tbl_recruit_info A LEFT JOIN tbl_hiretype B "
					+" ON A.recruit_field = B.hiretype_name "
					+" WHERE A.recruit_field like ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, "%"+ recruit_type_select + "%");
			
			rs = pstmt.executeQuery();
			
			search_refield = new ArrayList<>();
			
			while(rs.next()) {	
				search_recruit_field = new Recruit_INFO_DTO();
				
				search_recruit_field.setRecruit_no(rs.getString("recruit_no"));
				search_recruit_field.setRecruit_field(rs.getString("recruit_field"));
				search_recruit_field.setRecruit_title(rs.getString("recruit_title"));
				search_recruit_field.setRecruit_content(rs.getString("recruit_content"));
				search_recruit_field.setRecruit_registerday(rs.getString("recruit_registerday"));
				search_recruit_field.setRecruit_deadline(rs.getString("recruit_deadline"));
				search_recruit_field.setCareer(rs.getString("career"));
				search_recruit_field.setYear_salary(rs.getString("year_salary"));
				search_recruit_field.setRecruit_people(rs.getString("recruit_people"));
				search_recruit_field.setWork_day(rs.getString("work_day"));
				search_recruit_field.setWork_time(rs.getString("work_time"));
				search_recruit_field.setManager_email(rs.getString("manager_email"));
				search_recruit_field.setManager_name(rs.getString("manager_name"));
				
				search_refield.add(search_recruit_field);
				
			} // end of while(rs.next())
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		} // end of finally
		return search_refield;
	} // end of public List<Recruit_INFO_DTO> search_recruit_field(String recruit_type_select , Company_DTO company)


	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆

	
	
	// ◆◆◆ === 채용공고 수정하기전 단일 컬럼 미리 보여주기 === ◆◆◆ //
	@Override
	public String recruit_oneview(String company_id, String infoview, String input_rcno) {
		
		String result = "";
	      
		try {
			String sql = " select " + infoview 
	                  + " from tbl_recruit_info "
	                  + " where fk_company_id = ? and recruit_no = ? ";
	               
			pstmt = conn.prepareStatement(sql); // 우편배달부 = 서버.prepareStatement(전달할sql문)

			pstmt.setString(1, company_id);
			pstmt.setString(2, input_rcno);
	         
			rs = pstmt.executeQuery(); // SQL문 실행  
	         
			if(rs.next()) {
	    		  result =  rs.getString(1);
			}     
		} catch (SQLException e) {
        	e.printStackTrace();
		} finally { // 성공하든 안하든 무조건! 
			close();
		} // end of finally
      return result;
   } // end of public String recruit_oneview(String company_id, String infoview, String input_rcno)

	
	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	

	// ◆◆◆ === 채용공고 단일 컬럼 수정하기 === ◆◆◆ //
	@Override
	public int recruit_fixone(String infoview, String input_rcno, String fix_info) {
	      
	      int result = 0;
	      
	      try {
	         
	         String sql = " update tbl_recruit_info set " + infoview + " = ? "
	               + " where recruit_no = ? ";
	         
	         pstmt = conn.prepareStatement(sql); // 우편배달부 = 서버.prepareStatement(전달할sql문)

	         pstmt.setString(1, fix_info);
	         pstmt.setString(2, input_rcno);
	         
	         result = pstmt.executeUpdate(); // SQL문 실행  
	              
	      } catch (SQLException e) {
	            e.printStackTrace();
	      } finally { // 성공하든 안하든 무조건! 
	         close();
	      } // end of finally
	      return result;
	} // end of public int recruit_fixone(String company_id, String infoview, String input_rcno, String fix_info)

	
	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	
	
	// ◆◆◆ === 채용공고 전체 수정 === ◆◆◆ //
	@Override
	public int fix_recruit_all(Map<String, String> rc_allfixmap, String input_rcno) {
	      
	      int result = 0;
	      
	      try {
	         
	         String sql = " update tbl_recruit_info set recruit_title = ? , "
	               + " recruit_deadline = ? , manager_name = ? , manager_email = ? , "
	               + " recruit_content = ? , career = ? , year_salary = ? , recruit_people = ? , "
	               + " recruit_field = ? , work_day = ? , work_time = ? " 
	               + " where recruit_no = ? ";
	         
	         pstmt = conn.prepareStatement(sql); // 우편배달부 = 서버.prepareStatement(전달할sql문)

	         pstmt.setString(1, rc_allfixmap.get("recruit_title"));
	         pstmt.setString(2, rc_allfixmap.get("recruit_deadline"));
	         pstmt.setString(3, rc_allfixmap.get("manager_name"));
	         pstmt.setString(4, rc_allfixmap.get("manager_email"));
	         pstmt.setString(5, rc_allfixmap.get("recruit_content"));
	         pstmt.setString(6, rc_allfixmap.get("career"));
	         pstmt.setString(7, rc_allfixmap.get("year_salary"));
	         pstmt.setString(8, rc_allfixmap.get("recruit_people"));
	         pstmt.setString(9, rc_allfixmap.get("recruit_field"));
	         pstmt.setString(10, rc_allfixmap.get("work_day"));
	         pstmt.setString(11, rc_allfixmap.get("work_time"));
	         pstmt.setString(12, input_rcno);
	         
	         result = pstmt.executeUpdate(); // SQL문 실행  
	            
	      } catch (SQLException e) {
	            e.printStackTrace();
	      } finally { // 성공하든 안하든 무조건! 
	         close();
	      } // end of finally
	      return result;
	   } // end of public int fix_recruit_all(Map<String, String> rc_allfixmap, String input_rcno)

	
	
// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
	
	
	
	// ◆◆◆ === 로그인한 기업의 채용공고 삭제전 간단하게 보여주기  === ◆◆◆ //
	@Override
	public List<Recruit_INFO_DTO> get_recruitlist(Company_DTO company) {
	      Recruit_INFO_DTO rcinfo = null;
	         
	      List<Recruit_INFO_DTO> rclist = new ArrayList<>(); // 이거 설명
	            
	      try {
	         // SQL 문 작성
	         String sql = " select recruit_no , recruit_title, recruit_field , to_char(recruit_registerday, 'yyyy-mm-dd') as recruit_registerday , "
	               + " recruit_deadline , career,  manager_name , manager_email "
	               + " from tbl_recruit_info "
	               + " where fk_company_id = ? ";
	         
	          pstmt = conn.prepareStatement(sql); // 우편배달부 = 서버.prepareStatement(전달할sql문)
	          pstmt.setString(1, company.getCompany_id());
	          rs = pstmt.executeQuery(); // SQL문 실행  
	               
	          while (rs.next()) { // select 결과가 있다면~
	                  
	             rcinfo = new Recruit_INFO_DTO();
	                  
	             rcinfo.setRecruit_no(rs.getString("recruit_no")); 
	             rcinfo.setRecruit_title(rs.getString("recruit_title"));
	             rcinfo.setRecruit_field(rs.getString("recruit_field"));
	             rcinfo.setRecruit_registerday(rs.getString("recruit_registerday"));
	               rcinfo.setRecruit_deadline(rs.getString("recruit_deadline"));
	               rcinfo.setCareer(rs.getString("career"));
	               rcinfo.setManager_name(rs.getString("manager_name"));
	               rcinfo.setManager_email(rs.getString("manager_email"));
	                  
	               rclist.add(rcinfo);           
	          } // end of while        
	      } catch (SQLException e) {
	              e.printStackTrace();
	      } finally {    // 성공하든 안하든 무조건! 
	         close();   // 자원반납 하기
	      } // end of finally  
	      return rclist;
   	} // end of public List<Recruit_INFO_DTO> get_recruitlist(Company_DTO company) 



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆
		


	
	// ◆◆◆ === 채용공고 삭제 === ◆◆◆ //
	@Override
 	public int delete_recruitlist(String input_rcno) {
	      
	      int result = 0;
	      
	      try {
	            String sql = " delete from tbl_recruit_info where recruit_no = ? ";
	                      
	            pstmt = conn.prepareStatement(sql);   
	            
	            pstmt.setString(1, input_rcno);   
	            
	            result = pstmt.executeUpdate();      // SQL문 실행
	            
	      } catch (SQLException e) {
               e.printStackTrace(); 
	      } finally {
	    	  close();
	      }   // end try~catch~finally--------------------
	      return result;
	   } // end of public int delete_recruitlist(String input_rcno) 



// ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆


	// ◆◆◆ === 지원한 구직자 조회 === ◆◆◆ //
	@Override
	public void apply_user_search(Scanner sc,  User_DTO user, Company_DTO company) {
		// TODO Auto-generated method stub
		
	}	// end of public void apply_user_search(Scanner sc, User_DTO user, Company_DTO company, Recruit_INFO_DTO recruit)------


	



	
	
	

	
	

}
