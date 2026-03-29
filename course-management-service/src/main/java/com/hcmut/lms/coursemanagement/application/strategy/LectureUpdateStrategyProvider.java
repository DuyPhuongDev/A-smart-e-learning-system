package com.hcmut.lms.coursemanagement.application.strategy;

import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provider class that manages and provides the appropriate update strategy
 * based on the lecture type.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LectureUpdateStrategyProvider {
    
    private final List<LectureUpdateStrategy> strategies;
    private Map<LectureType, LectureUpdateStrategy> strategyMap;
    
    /**
     * Initializes the strategy map after all strategies are injected.
     */
    private void initializeStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            LectureUpdateStrategy::getSupportedLectureType,
                            Function.identity()
                    ));
            log.info("Initialized {} lecture update strategies", strategyMap.size());
        }
    }
    
    /**
     * Gets the appropriate update strategy for the given lecture type.
     * 
     * @param lectureType the type of lecture
     * @return the update strategy for the lecture type
     * @throws IllegalArgumentException if no strategy is found for the lecture type
     */
    public LectureUpdateStrategy getStrategy(LectureType lectureType) {
        initializeStrategyMap();
        
        LectureUpdateStrategy strategy = strategyMap.get(lectureType);
        
        if (strategy == null) {
            throw new IllegalArgumentException("No update strategy found for lecture type: " + lectureType);
        }
        
        return strategy;
    }
    
    /**
     * Checks if a strategy exists for the given lecture type.
     * 
     * @param lectureType the type of lecture
     * @return true if a strategy exists, false otherwise
     */
    public boolean hasStrategy(LectureType lectureType) {
        initializeStrategyMap();
        return strategyMap.containsKey(lectureType);
    }
}

