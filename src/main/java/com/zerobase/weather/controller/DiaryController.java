package com.zerobase.weather.controller;

import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiaryController {
	private final DiaryService diaryService;

	public DiaryController(DiaryService diaryService) {
		this.diaryService = diaryService;
	}

	@ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장")
	@PostMapping("/create/diary")
	void createDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE)
		@ApiParam(value = "일기의 날자",example = "2020-10-25")LocalDate date,
		@RequestBody @ApiParam(value = "일기 내용 ",example = "오늘은 날씨가 좋다")String text){
		diaryService.createDiary(date,text);
	}

	@ApiOperation(value = "입력한 날자의 일기를 가져 옵니다.")
	@GetMapping("/read/diary")
	List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "일기의 날자",example = "2020-10-25")LocalDate date) {
		return diaryService.readDiary(date);
	}

	@ApiOperation(value = "입력한 기간의 일기를 가져옵니다.")
	@GetMapping("/read/diaries")
	List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 시작 날",example = "2020-10-25") LocalDate startDate,
							@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 마지막 날",example = "2020-10-26")LocalDate endDate) {
		return diaryService.readDiaries(startDate,endDate);
	}

	@ApiOperation(value = "입력한 날짜의 일기를 텍스트로 수정합니다.")
	@PutMapping("/update/diary")
	void updateDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "일기의 날자",example = "2020-10-25")LocalDate date,
					 @RequestBody @ApiParam(value = "수정할 내용 입력",example = "오늘은 날씨가 나쁘다") String text) {
		diaryService.updateDiary(date,text);
	}

	@ApiOperation(value = "입력한 날짜의 일기를 삭제합니다.")
	@DeleteMapping("/delete/diary")
	void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "일기의 날자",example = "2020-10-25")LocalDate date) {
		diaryService.deleteDiary(date);
	}
}
