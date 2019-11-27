/**
 * @File component renders a text preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Row, Typography } from "antd";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getNewChunkSize, statusText } from "../../analysis/shared-preview";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { isAdmin } from "../../../contexts/AnalysisContext";
import { OutputFileHeader } from "../../../components/OutputFiles";

const scrollableDivHeight = 300;

const TextOutputWrapper = styled.div`
  height: ${scrollableDivHeight}px;
  width: 100%;
  overflow: auto;
  margin-bottom: ${SPACE_XS};
  border: solid 1px #bdc3c7;
  overflow-wrap: break-word;
`;

export function AnalysisTextPreview({ output }) {
  const [fileRows, setFileRows] = useState([]);
  const [filePointer, setFilePointer] = useState(0);
  const [savedText, setSavedText] = useState("");
  const chunkSize = 8192;

  /*
   * Get n bytes of text file output data on load and set
   * the fileRows local state to this data.
   */
  useEffect(() => {
    getDataViaChunks({
      submissionId: output.analysisSubmissionId,
      fileId: output.id,
      seek: 0,
      chunk: getNewChunkSize(0, output.fileSizeBytes, chunkSize)
    }).then(data => {
      setSavedText(data.text);
      setFilePointer(data.filePointer);
      setFileRows(data.text);
      document.getElementById(
        `${output.filename}-preview-status`
      ).innerText = statusText(data.filePointer, output.fileSizeBytes);
    });
  }, []);

  /*
   * Get n bytes of text file output data on scroll and set
   * the fileRows local state to this data added to savedText.
   */
  function loadMoreData() {
    const scollElement = document.getElementById(
      `text-${output.filename.split(".")[0]}`
    );

    if (
      scollElement.scrollTop + scrollableDivHeight >=
        scollElement.scrollHeight &&
      getNewChunkSize(filePointer, output.fileSizeBytes, chunkSize) >= 0
    ) {
      getDataViaChunks({
        submissionId: output.analysisSubmissionId,
        fileId: output.id,
        seek: filePointer,
        chunk: getNewChunkSize(filePointer, output.fileSizeBytes, chunkSize)
      }).then(data => {
        if (data.text !== null) {
          setSavedText(savedText + data.text);
          setFilePointer(data.filePointer);
          setFileRows(savedText + data.text);
          document.getElementById(
            `${output.filename}-preview-status`
          ).innerText = statusText(data.filePointer, output.fileSizeBytes);
        }
      });
    }
  }

  /*
   * Displays the scrollable div with text output as well
   * as the name of the file and a download button for
   * the file
   */
  function displayTextOutput() {
    if (fileRows.length > 0) {
      return (
        <div>
          <Row>
            <OutputFileHeader output={output} />
          </Row>
          {isAdmin ? (
            <Row>
              <TextOutputWrapper
                id={`text-${output.filename.split(".")[0]}`}
                style={{ padding: SPACE_XS }}
                onScroll={() => loadMoreData()}
              >
                {fileRows}
              </TextOutputWrapper>
              <div
                style={{ fontWeight: "bold" }}
                id={`${output.filename}-preview-status`}
              ></div>
              <Divider />
            </Row>
          ) : null}
        </div>
      );
    }
  }

  return <>{fileRows !== null ? displayTextOutput() : <ContentLoading />}</>;
}
