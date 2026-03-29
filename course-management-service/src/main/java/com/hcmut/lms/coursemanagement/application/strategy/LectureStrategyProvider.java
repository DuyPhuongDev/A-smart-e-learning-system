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
 * Generic provider class that manages and provides strategies based on lecture type.
 * This can be used for any strategy type that extends LectureStrategy.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LectureStrategyProvider {
    
    private final List<LectureStrategy> strategies;
    private Map<LectureType, LectureStrategy> strategyMap;
    
    /**
     * Initializes the strategy map after all strategies are injected.
     */
    private void initializeStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            LectureStrategy::getSupportedLectureType,
                            Function.identity(),
                            (existing, replacement) -> {
                                log.warn("Duplicate strategy found for lecture type: {}. Using: {}", 
                                        existing.getSupportedLectureType(), replacement.getClass().getSimpleName());
                                return replacement;
                            }
                    ));
            log.info("Initialized {} lecture strategies", strategyMap.size());
        }
    }
    
    /**
     * Gets the appropriate strategy for the given lecture type.
     * 
     * @param lectureType the type of lecture
     * @return the strategy for the lecture type
     * @throws IllegalArgumentException if no strategy is found for the lecture type
     */
    public LectureStrategy getStrategy(LectureType lectureType) {
        initializeStrategyMap();
        
        LectureStrategy strategy = strategyMap.get(lectureType);
        
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for lecture type: " + lectureType);
        }
        
        return strategy;
    }
    
    /**
     * Gets a strategy of a specific type for the given lecture type.
     * 
     * @param lectureType the type of lecture
     * @param strategyClass the class of the strategy to retrieve
     * @param <T> the type of strategy
     * @return the strategy of the specified type
     * @throws IllegalArgumentException if no strategy is found or if the strategy is not of the expected type
     */
    @SuppressWarnings("unchecked")
    public <T extends LectureStrategy> T getStrategy(LectureType lectureType, Class<T> strategyClass) {
        LectureStrategy strategy = getStrategy(lectureType);
        
        if (!strategyClass.isInstance(strategy)) {
            throw new IllegalArgumentException(
                    String.format("Strategy for lecture type %s is not of type %s. Found: %s",
                            lectureType, strategyClass.getSimpleName(), strategy.getClass().getSimpleName()));
        }
        
        return (T) strategy;
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

