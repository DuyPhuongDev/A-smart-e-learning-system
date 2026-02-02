package com.hcmut.lms.learning.client.dto;

import java.util.UUID;

public record ClassEnrollStatus(
        UUID id,
        boolean exist,
        ClassStatus status,
        boolean isOfficial,
        int maxStudents,
        int currentStudents
) {

    public static ClassEnrollStatus unavailable(UUID classId) {
        return new ClassEnrollStatus(classId, false, ClassStatus.UNAVAILABLE, false, 0, 0);
    }

    public boolean canEnroll(){
        return exist && currentStudents <  maxStudents && status.equals(ClassStatus.OPEN);
    }

    public boolean isFull(){
        return currentStudents >= maxStudents;
    }
}
