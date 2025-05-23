package io.github.pmckeown.dependencytrack.suppressions;

/**
 * List of valid justifications for an analysis as of Dependency Track version 4.12.5.
 *
 * @author Thomas Hucke
 */
public enum AnalysisJustificationEnum {
    CODE_NOT_PRESENT,
    CODE_NOT_REACHABLE,
    REQUIRES_CONFIGURATION,
    REQUIRES_DEPENDENCY,
    REQUIRES_ENVIRONMENT,
    PROTECTED_BY_COMPILER,
    PROTECTED_AT_RUNTIME,
    PROTECTED_AT_PERIMETER,
    PROTECTED_BY_MITIGATING_CONTROL,
    NOT_SET
}
