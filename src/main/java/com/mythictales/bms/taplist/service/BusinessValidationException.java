package com.mythictales.bms.taplist.service;

public class BusinessValidationException extends RuntimeException {
  private final Object details;

  public BusinessValidationException(String message) {
    super(message);
    this.details = null;
  }

  public BusinessValidationException(String message, Object details) {
    super(message);
    this.details = details;
  }

  public Object getDetails() {
    return details;
  }
}
