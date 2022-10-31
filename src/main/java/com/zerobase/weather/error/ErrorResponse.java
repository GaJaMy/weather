package com.zerobase.weather.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	String errorMessage;
	String errorType;
}
