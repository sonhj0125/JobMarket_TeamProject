package company.model;

import java.util.Scanner;

import company.domain.Company_DTO;
import company.domain.Recruit_INFO_DTO;
import user.domain.User_DTO;

public interface Recruit_DAO {

	// ◆◆◆ === 지원한 구직자 조회 === ◆◆◆ //
	void apply_user_search(Scanner sc, User_DTO user, Company_DTO company);

	// ◆◆◆ === 채용공고 등록 === ◆◆◆ //
	void recruit_register(Scanner sc, Company_DTO company);

	// ◆◆◆ === 채용공고 조회 === ◆◆◆ //
	void recruit_information(Scanner sc, Company_DTO company);

	// ◆◆◆ === 채용공고 수정 === ◆◆◆ //
	void recruit_fix(Scanner sc, Company_DTO company);

	// ◆◆◆ === 채용공고 삭제 === ◆◆◆ //
	void recruit_delete(Scanner sc, Company_DTO company);

}
