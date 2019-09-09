/*
 * This file displays the steps of the analysis
 * (Queued, Preparing, Submitting, Running,
 * Completing, Completed)
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext } from "react";
import { Steps, Icon } from "antd";
import { AnalysisContext, stateMap } from "../../../contexts/AnalysisContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { SPACE_MD } from "../../../styles/spacing";

const Step = Steps.Step;

export function AnalysisSteps() {
  const { analysisContext } = useContext(AnalysisContext);
  return (
    <>
      <Steps
        current={stateMap[analysisContext.analysisState]}
        status={analysisContext.isError ? "error" : "finish"}
        style={{ paddingBottom: SPACE_MD }}
      >
        <Step
          title={getI18N("analysis.state.NEW")}
          icon={
            analysisContext.analysisState == "NEW" ? (
              <Icon type="loading" />
            ) : null
          }
        />
        <Step
          title={getI18N("analysis.state.PREPARING")}
          icon={
            analysisContext.analysisState == "PREPARING" ? (
              <Icon type="loading" />
            ) : null
          }
        />
        <Step
          title={getI18N("analysis.state.SUBMITTING")}
          icon={
            analysisContext.analysisState == "SUBMITTING" ? (
              <Icon type="loading" />
            ) : null
          }
        />
        <Step
          title={getI18N("analysis.state.RUNNING")}
          icon={
            analysisContext.analysisState == "RUNNING" ? (
              <Icon type="loading" />
            ) : null
          }
        />
        <Step
          title={getI18N("analysis.state.COMPLETING")}
          icon={
            analysisContext.analysisState == "COMPLETING" ? (
              <Icon type="loading" />
            ) : null
          }
        />
        <Step
          title={getI18N("analysis.state.COMPLETED")}
          icon={
            analysisContext.analysisState == "COMPLETED" ? (
              <Icon type="loading" />
            ) : null
          }
        />
      </Steps>
    </>
  );
}
