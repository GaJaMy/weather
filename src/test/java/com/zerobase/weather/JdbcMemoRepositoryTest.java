package com.zerobase.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.weather.domain.Memo;
import com.zerobase.weather.repository.JdbcMemoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional// -> 테스트 코드가 다 작업이 끝나도 원상태로 돌려 놓음
public class JdbcMemoRepositoryTest {
	@Autowired
	JdbcMemoRepository jdbcMemoRepository;

	@Test
	void insertMemoTest() {
		//given
		Memo newMemo = new Memo(2, "insertMemoTest");

		//when
		jdbcMemoRepository.save(newMemo);

		//then
		Optional<Memo> result = jdbcMemoRepository.findById(2);
		assertEquals(result.get().getText() , "insertMemoTest");
	}

	@Test
	void findAllMemoTest() {
		List<Memo> memoList = jdbcMemoRepository.findAll();
		System.out.println(memoList);
		assertNotNull(memoList);
	}
}
