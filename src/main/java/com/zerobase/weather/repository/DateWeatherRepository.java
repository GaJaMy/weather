package com.zerobase.weather.repository;

import com.zerobase.weather.domain.DateWeather;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
	List<DateWeather> findAllByDate(LocalDate localDate);
}
