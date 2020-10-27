/*
 * This file is responsible for displaying the
 * tabs required and default renders the
 * FastQCCharts component.
 */

import React, { Suspense, useEffect, useState } from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { Link, Location, Router } from "@reach/router";
import { Badge, Menu } from "antd";
import { ContentLoading } from "../../../components/loader";

import { SPACE_MD, SPACE_XS } from "../../../styles/spacing"
import { getFastQCDetails } from "../../../apis/files/sequence-files";
import {
  seqObjId,
  seqFileId,
  urlMatch
} from "../fastqc-constants";
import { InfoAlert } from "../../../components/alerts";
import { blue6 } from "../../../styles/colors";

const FastQCDetails = React.lazy(() => import("./FastQCDetails"));
const FastQCCharts = React.lazy(() => import("./FastQCCharts"));
const OverRepresentedSequences = React.lazy(() => import("./OverRepresentedSequences"));

export default function FastQC() {
  const [loading, setLoading] = useState(true);
  const [fastQC, setFastQC] = useState({});
  const [file, setFile] = useState({});
  const DEFAULT_URL = setBaseUrl(urlMatch[0]);


  const [selectedKeys, setSelectedKeys] = React.useState(() => {
    // Create a regex from the string with an additional string at the end
    const regexStr = urlMatch[0] + "/(\\w*)?";
    const urlRegex = new RegExp(regexStr, "i");

    const urlMatchRegex = window.location.href.match(urlRegex);
    // Grab the key if it exists from the match array otherwise default to 'charts'
    return urlMatchRegex !== null ? urlMatchRegex[1] : "charts";
  });

  useEffect(() => {
    getFastQCDetails(seqObjId, seqFileId).then(({ analysisFastQC, sequenceFile }) => {
      setFile(sequenceFile);
      setFastQC(analysisFastQC);
      setLoading(false);
    });
  }, []);

  return (
    <PageWrapper title={file.fileName}>
      {loading ?
        <ContentLoading message={i18n("FastQC.fetchingData")} />
        :
         fastQC ?
          <div>
            <Location>
              {() => {
                return (
                  <Menu
                    mode="horizontal"
                    selectedKeys={[selectedKeys]}
                    className="t-fastQC-nav"
                  >
                    <Menu.Item key="charts">
                      <Link to={`${DEFAULT_URL}/charts`}
                            onClick={() => setSelectedKeys("charts")}>
                        {i18n("FastQC.charts")}
                      </Link>
                    </Menu.Item>
                    <Menu.Item key="overrepresented">
                        <Link to={`${DEFAULT_URL}/overrepresented`}
                              onClick={() => setSelectedKeys("overrepresented")}>
                          {i18n("FastQC.overrepresentedSequences")}
                          <Badge count={fastQC.overrepresentedSequences.length}
                                 showZero
                                 style={{ backgroundColor: blue6, marginLeft: SPACE_XS }}
                                 className="t-overrepresented-sequences-count"
                          />
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="details">
                      <Link to={`${DEFAULT_URL}/details`}
                            onClick={() => setSelectedKeys("details")}>
                        {i18n("FastQC.details")}
                      </Link>
                    </Menu.Item>
                  </Menu>
                );
              }}
            </Location>
            <Suspense fallback={<ContentLoading />}>
              <Router style={{paddingTop: SPACE_MD}}>
                <FastQCCharts path={`${DEFAULT_URL}/charts`} default
                              key="charts"/>
                <OverRepresentedSequences path={`${DEFAULT_URL}/overrepresented`}
                                          key="overrepresented"/>
                <FastQCDetails path={`${DEFAULT_URL}/details`} key="details"/>
              </Router>
            </Suspense>
          </div>
            :
          <div>
            <InfoAlert
              message={i18n("FastQC.noResults")}
              style={{marginBottom: SPACE_XS}}
              className="t-fastQC-no-run"
            />
          </div>
      }
    </PageWrapper>
  );
}