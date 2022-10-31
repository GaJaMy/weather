package com.zerobase.weather.config;

import com.zerobase.weather.WeatherApplication;
import com.zerobase.weather.error.ErrorResponse;
import com.zerobase.weather.error.InvalidDate;
import com.zerobase.weather.error.NotFoundDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Exception handleAllException() {
		logger.error("error from GlobalErrorHandler");
		return new Exception();
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(InvalidDate.class)
	public InvalidDate handleTest(InvalidDate e) {
		return e;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(NotFoundDate.class)
	public NotFoundDate notFoundExceptionHandle(NotFoundDate e) {
		return e;
	}
}
