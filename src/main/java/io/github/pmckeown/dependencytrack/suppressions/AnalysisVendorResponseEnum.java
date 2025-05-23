package io.github.pmckeown.dependencytrack.suppressions;

/**
 * List of valid vendor response values for an analysis as of Dependency Track version 4.12.5.
 *
 * @author Thomas Hucke
 */
public enum AnalysisVendorResponseEnum {
    CAN_NOT_FIX,
    WILL_NOT_FIX,
    UPDATE,
    ROLLBACK,
    WORKAROUND_AVAILABLE,
    NOT_SET
}
