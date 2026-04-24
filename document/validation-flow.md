Validation Flow

4 checks run in parallel (GoalFeasibilityCheckService):

┌─────┬─────────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│  #  │        Validator        │                                                  Condition to Pass                                                  │
├─────┼─────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 1   │ Credit/Time             │ remainingCredits ≤ maxCreditsAvailable (main + summer semesters)                                                    │
├─────┼─────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 2   │ GPA Requirement         │ requiredGpa ≤ 4.00 — where requiredGpa = (targetGPA × totalCredits − currentGPA × earnedCredits) / remainingCredits │
├─────┼─────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 3   │ Prerequisite Chain      │ longestPrerequisiteChain ≤ availableSemesters                                                                       │
├─────┼─────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 4   │ Graduation Requirements │ No "at-risk" requirements (incomplete with no plan)                                                                 │
└─────┴─────────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

  ---
Feasibility Classification

┌────────┬─────────────────────────────────────────────┐
│ Level  │                  Condition                  │
├────────┼─────────────────────────────────────────────┤
│ WEAK   │ Any of the 4 checks fails                   │
├────────┼─────────────────────────────────────────────┤
│ GOOD   │ All checks pass AND probabilityScore ≥ 0.50 │
├────────┼─────────────────────────────────────────────┤
│ MEDIUM │ All checks pass AND probabilityScore < 0.50 │
└────────┴─────────────────────────────────────────────┘

  ---
Recommendations by Feasibility Level & Top Priority

Top Priority Resolution

Student sets order for 3 priorities via LearningGoal:
- attemptTargetGpaOrder → GPA priority
- focusOnTargetOccupation → Knowledge priority
- completedOnTime → Time priority

Lowest order number = top priority.

  ---
GOOD Feasibility (all checks pass, probability ≥ 0.50)

┌──────────────┬─────┬─────────────────────┬──────────────────────────────────────────────────────────────┐
│ Top Priority │  #  │ RecommendationType  │                         Description                          │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ GPA          │ 1   │ MAINTAIN_LOAD       │ Maintain moderate workload to avoid overload and protect GPA │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ GPA          │ 2   │ FOCUS_HIGH_CREDIT   │ Focus on high-credit courses (they impact GPA most)          │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ Knowledge    │ 1   │ ADD_ELECTIVES       │ Consider advanced electives to expand knowledge              │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ Knowledge    │ 2   │ ACADEMIC_ACTIVITIES │ Join academic clubs or research activities                   │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ Time         │ 1   │ STICK_TO_PLAN       │ Follow study plan strictly, avoid delaying mandatory courses │
├──────────────┼─────┼─────────────────────┼──────────────────────────────────────────────────────────────┤
│ Time         │ 2   │ AVOID_LONG_BREAKS   │ Limit summer breaks or long interruptions                    │
└──────────────┴─────┴─────────────────────┴──────────────────────────────────────────────────────────────┘

  ---
MEDIUM Feasibility (all checks pass, probability < 0.50)

Common (all priorities):
1. REVIEW_LOAD — Review semester workload and adjust credits to reduce risk
2. USE_SUMMER — Use summer semesters to distribute load
3. PRIORITIZE_CORE — Prioritize core and high-credit courses

Priority-specific:

┌──────────────┬─────┬───────────────────────────┬────────────────────────────────────────────────────────────────────┐
│ Top Priority │  #  │    RecommendationType     │                            Description                             │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ GPA          │ 4   │ REDUCE_LOAD_FOR_GPA       │ Reduce credits per semester to focus on each course                │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ GPA          │ 5   │ CHOOSE_MANAGEABLE_COURSES │ Choose moderately difficult courses for stable results             │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ Knowledge    │ 4   │ REDUCE_FOR_DEPTH          │ Reduce credits to allow deeper research                            │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ Knowledge    │ 5   │ CONSIDER_DELAY            │ Consider extending graduation by one semester to avoid overload    │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ Time         │ 4   │ INCREASE_LOAD             │ Slightly increase credits within limits + use 1-2 summer semesters │
├──────────────┼─────┼───────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ Time         │ 5   │ ACCEPT_GPA_TRADEOFF       │ Adjust GPA target if gap between predicted and target GPA is large │
└──────────────┴─────┴───────────────────────────┴────────────────────────────────────────────────────────────────────┘

  ---
WEAK Feasibility (at least one check failed)

Failure-specific recommendations (added first, based on which check failed):

┌──────────────┬─────┬──────────────────────────┬──────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ Failed Check │  #  │    RecommendationType    │                                           Dynamic Message                                            │
├──────────────┼─────┼──────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Credit/Time  │ 1   │ EXTEND_TIMELINE          │ "Remaining credits (X) exceed max achievable (Y). Need to extend timeline or increase intensity."    │
├──────────────┼─────┼──────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ GPA          │ 2   │ LOWER_GPA_TARGET         │ "To reach target GPA X.XX, need avg Y.YY for remaining courses (exceeds 4.00). Consider adjusting."  │
├──────────────┼─────┼──────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Prerequisite │ 3   │ EXTEND_FOR_PREREQUISITES │ "Longest prerequisite chain (N semesters) exceeds remaining (M). Need to extend by (N-M) semesters." │
└──────────────┴─────┴──────────────────────────┴──────────────────────────────────────────────────────────────────────────────────────────────────────┘

Priority-specific recommendations:

┌──────────────┬─────┬──────────────────────────┬───────────────────────────────────────────────────────────────────┐
│ Top Priority │  #  │    RecommendationType    │                            Description                            │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ GPA          │ 4   │ EXTEND_REDUCE_PRESSURE   │ Extend timeline to reduce per-semester pressure                   │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ GPA          │ 5   │ RECONSIDER_GPA           │ If can't extend, adjust GPA target to be more feasible            │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ Knowledge    │ 4   │ REDUCE_LOAD_ACCEPT_DELAY │ Reduce credits to study deeper, accept delayed graduation         │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ Knowledge    │ 5   │ FOCUS_CORE               │ Focus on foundational/core courses instead of doing too much      │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ Time         │ 4   │ MAX_LOAD_AND_SUMMER      │ Max out credits and use all summer semesters                      │
├──────────────┼─────┼──────────────────────────┼───────────────────────────────────────────────────────────────────┤
│ Time         │ 5   │ LOWER_GPA_FOR_TIME       │ Lower GPA target to reduce course-failure risk while keeping pace │
└──────────────┴─────┴──────────────────────────┴───────────────────────────────────────────────────────────────────┘

  ---
Warnings (separate from recommendations)

┌──────────┬─────────────────────────────────────────────────────────────┬─────────────────────────────────────────────────────────────────┐
│ Severity │                          Condition                          │                             Message                             │
├──────────┼─────────────────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ HIGH     │ At-risk graduation requirements (incomplete + no plan)      │ "These graduation requirements have no completion plan: [list]" │
├──────────┼─────────────────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ MEDIUM   │ Missing but planned requirements (only if no HIGH warnings) │ "Student still needs to complete: [list]. Need to plan these."  │
└──────────┴─────────────────────────────────────────────────────────────┴─────────────────────────────────────────────────────────────────┘

  ---
Total unique recommendation types: 24 (as defined in RecommendationType.java).