package com.apotheosis.acceleration.util;

public class BadDataException extends Exception
{
	private static final long serialVersionUID = -1838125225428773566L;

	public BadDataException() { };
	
	public BadDataException(String message)
	{
		super(message);
	}
}
