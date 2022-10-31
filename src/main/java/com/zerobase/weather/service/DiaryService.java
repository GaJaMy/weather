package com.zerobase.weather.service;

import com.zerobase.weather.WeatherApplication;
import com.zerobase.weather.domain.DateWeather;
import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.error.InvalidDate;
import com.zerobase.weather.error.NotFoundDate;
import com.zerobase.weather.repository.DateWeatherRepository;
import com.zerobase.weather.repository.DiaryRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DiaryService {
	private final DiaryRepository diaryRepository;
	private final DateWeatherRepository dateWeatherRepository;

	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

	@Value("${openweathermap.key}")
	String apiKey;

	public DiaryService(DiaryRepository diaryRepository,
		DateWeatherRepository dateWeatherRepository) {
		this.diaryRepository = diaryRepository;
		this.dateWeatherRepository = dateWeatherRepository;
	}

	@Transactional
	@Scheduled(cron = "0 0 1 * * *")
	public void saveWeatherDate(){
		logger.info("날씨 자동 저장 : " + LocalDate.now().toString());
		dateWeatherRepository.save(getWeatherFromApi());
	}

	private DateWeather getDateWeather(LocalDate date) {
		List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
		if (dateWeatherListFromDB.size() == 0) {
			logger.info("get weather form API");
			return getWeatherFromApi();
		} else {
			logger.info("get weather form DB");
			DateWeather result = dateWeatherListFromDB.get(0);

			logger.info(" ===== DB Data ====== " );
			logger.info(" Date: " + result.getDate());
			logger.info(" Weather: " + result.getWeather());
			logger.info(" Icon: " + result.getIcon());
			logger.info(" Temperature " + result.getTemperature());
			logger.info(" ===== DB Data ====== " );

			return dateWeatherListFromDB.get(0);
		}
	}
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void createDiary(LocalDate date, String text) {
		logger.info("started to create diary");
		// 날씨 데이터 가져오기(API가 아닌 DB에서 가져오기)
		DateWeather dateWeather = getDateWeather(date);

		// 파싱된 데이터 + 일기 값 db에 삽입
		logger.info("insult diary data, text: " + text);
		Diary nowDiary = new Diary();
		nowDiary.setDateWeather(dateWeather);
		nowDiary.setText(text);

		diaryRepository.save(nowDiary);
		logger.info("end to create diary");
	}

	private DateWeather getWeatherFromApi() {
		// open weather map에서 날씨 데이터 가져오기
		String weatherData = getWeatherString();

		logger.info("API response + " + weatherData);

		// 받아온 날씨 json 파싱
		Map<String, Object> parsedWeather = parseWeather(weatherData);
		DateWeather dateWeather = new DateWeather();
		dateWeather.setDate(LocalDate.now());
		dateWeather.setWeather(parsedWeather.get("main").toString());
		dateWeather.setIcon(parsedWeather.get("icon").toString());
		dateWeather.setTemperature((Double) parsedWeather.get("temp"));


		logger.info(" ===== Parsed Data ====== " );
		logger.info(" Date: " + dateWeather.getDate());
		logger.info(" Weather: " + dateWeather.getWeather());
		logger.info(" Icon: " + dateWeather.getIcon());
		logger.info(" Temperature " + dateWeather.getTemperature());
		logger.info(" ===== Parsed Data ====== " );
		return dateWeather;
	}

	private String getWeatherString() {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}

			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			return response.toString();
		} catch (Exception e) {
			return "failed to get response";
		}
	}

	private Map<String,Object> parseWeather(String jsonString) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> resultMap = new HashMap<>();

		JSONObject mainData = (JSONObject) jsonObject.get("main");
		resultMap.put("temp",mainData.get("temp"));
		JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
		JSONObject weatherData = (JSONObject) weatherArray.get(0);
		resultMap.put("main",weatherData.get("main"));
		resultMap.put("icon",weatherData.get("icon"));
		return resultMap;
	}


	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		logger.info("readDiary start");
		Optional<Diary> optionalDiary = diaryRepository.findById(1);
		if (!optionalDiary.isPresent()) {
			logger.error("not found diary Data Date : " + date);
			throw new NotFoundDate("일기 정보가 없습니다.");
		}
		Diary diary = optionalDiary.get();

		if (date.isAfter(LocalDate.now().plusDays(1))) {
			logger.error("invalid date cause: future date, date : " + date);
			throw new InvalidDate("미래의 일기는 알 수 없습니다.");
		} else if (diary.getDate().isAfter(date)) {
			logger.error("invalid date cause: past date, date : " + date);
			throw new InvalidDate("첫 일기 이전 정보가 없습니다.");
		}
		List<Diary> diaryList = diaryRepository.findAllByDate(date);
		if (diaryList.isEmpty()) {
			logger.error("not found diary Date Date : " + date);
			throw new NotFoundDate("해당 일기가 없습니다.");
		}

		logger.info("success readDiary");
		return diaryList;
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
		logger.info("readDiaries start");
		List<Diary> diaryList = diaryRepository.findAllByDateBetween(startDate,endDate);

		if (diaryList.isEmpty()) {
			logger.error("not found diary Data startDate :" + startDate + ", endDate :" + endDate);
			throw new NotFoundDate("일기 정보가 없습니다.");
		}

		if (startDate.isAfter(endDate)) {
			logger.error("Invalid date cause : startDate is future than endDate (startDate:"
				+ startDate + ", endDate :" + endDate +")");
			throw new InvalidDate("날자 범위가 잘못 되었습니다.");
		}

		logger.info("success readDiaries");
		return diaryList;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void updateDiary(LocalDate date, String text) {
		logger.info("update start");
		Diary nowDiary = diaryRepository.getFirstByDate(date);

		if (nowDiary == null) {
			logger.error("not found diary data");
			throw new NotFoundDate("일기 정보가 없습니다.");
		}
		logger.info("update Data : " + text);
		nowDiary.setText(text);
		diaryRepository.save(nowDiary);
		logger.info("success update");
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void deleteDiary(LocalDate date) {
		logger.info("delete start");
		Diary nowDiary = diaryRepository.getFirstByDate(date);
		if (nowDiary == null) {
			logger.error("not found diary data");
			throw new NotFoundDate("일기 정보가 없습니다.");
		}
		diaryRepository.deleteAllByDate(date);
		logger.info("success delete");
	}
}
