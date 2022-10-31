package com.zerobase.weather.error;

public class InvalidDate extends RuntimeException{
	public InvalidDate(String Message) {
		super(Message);
	}
}
