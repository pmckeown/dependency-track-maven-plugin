package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.CommonConfig;

/**
 * @author Paul McKeown
 */
class ScoreConfig {

    private CommonConfig commonConfig;
    private Integer inheritedRiskScoreThreshold;

    public ScoreConfig(CommonConfig commonConfig, Integer inheritedRiskScoreThreshold) {
        this.commonConfig = commonConfig;
        this.inheritedRiskScoreThreshold = inheritedRiskScoreThreshold;
    }

    public CommonConfig common() {
        return commonConfig;
    }

    public Integer getInheritedRiskScoreThreshold() {
        return inheritedRiskScoreThreshold;
    }
}
