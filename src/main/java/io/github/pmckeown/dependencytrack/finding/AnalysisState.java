package io.github.pmckeown.dependencytrack.finding;

public enum AnalysisState {
    NOT_AFFECTED,
    FALSE_POSITIVE,
    IN_TRIAGE,
    EXPLOITABLE,
    NOT_SET
}
