package com.hcmut.lms.personalization.exception;

/**
 * Thrown when curriculum prerequisite data contains a cycle,
 * making scheduling impossible.
 */
public class CyclicDependencyException extends RuntimeException {

  private final String subjectId;

  public CyclicDependencyException(String subjectId, String message) {
    super(message);
    this.subjectId = subjectId;
  }

  public String getSubjectId() {
    return subjectId;
  }
}