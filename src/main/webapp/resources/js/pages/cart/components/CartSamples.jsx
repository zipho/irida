import React from "react";

import PropTypes from "prop-types";
import { connect } from "react-redux";
import styled from "styled-components";
import { SPACE_SM } from "../../../styles/spacing";
import { blue6, grey1, grey3, red4, red6 } from "../../../styles/colors";
import AutoSizer from "react-virtualized-auto-sizer";
import { Avatar, Button, Input, List, Radio, Table, Tag, Tree } from "antd";
import { FixedSizeList as VList } from "react-window";
import { actions } from "../../../redux/reducers/cart";
import { sampleDetailsActions } from "../../../components/SampleDetails";
import { SampleRenderer } from "./SampleRenderer";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconFile, IconLinkOut } from "../../../components/icons/Icons";

const { Search } = Input;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 400px;
`;

const CartTools = styled.div`
  padding: 0 ${SPACE_SM};
  height: 65px;
  border-bottom: ${BORDERED_LIGHT};
  display: flex;
  align-items: center;

  .ant-input {
    background-color: ${grey1};

    &:hover {
      background-color: ${grey3};
    }

    &:focus {
      border: 1px solid ${blue6};
      background-color: ${grey1};
    }
  }
`;

const CartSamplesWrapper = styled.div`
  flex-grow: 1;
  background-color: white;
`;

const ButtonsPanelBottom = styled.div`
  height: 60px;
  padding: ${SPACE_SM};
  border-top: ${BORDERED_LIGHT};
  display: flex;
  justify-content: center;
  align-items: center;
`;

const EmptyCartButton = styled(Button)`
  background-color: ${red4};
  color: ${grey1};

  &:hover {
    background-color: ${red6};
    color: ${grey1};
  }
`;

function CartSamplesComponent({
  samples,
  applyFilter,
  emptyCart,
  displaySample,
  removeSample,
  removeProject,
}) {
  const filterSamples = (e) => applyFilter(e.target.value);

  const removeOneSample = (sample) => removeSample(sample);

  const removeOneProject = (id) => removeProject(id);

  const renderSample = ({ index, data, style }) => (
    <SampleRenderer
      rowIndex={index}
      data={samples[index]}
      style={style}
      displaySample={displaySample}
      removeSample={removeOneSample}
      removeProject={removeOneProject}
    />
  );

  // const formattedSamples = samples.map((project) => ({
  //   title: (
  //     <span>
  //       {project.label}
  //       <Button
  //         size="small"
  //         type="link"
  //         onClick={(e) => e.stopPropagation()}
  //         href={setBaseUrl(`/projects/${project.id}`)}
  //         icon={<IconLinkOut />}
  //       />
  //     </span>
  //   ),
  //   key: `project-${project.id}`,
  //   children: project.samples.map((sample) => ({
  //     title: sample.label,
  //     key: `sample-${sample.id}`,
  //   })),
  // }));

  const PairedFileList = ({ pairs }) => (
    <Radio.Group>
      <List
        style={{ marginLeft: 30 }}
        dataSource={pairs}
        renderItem={(pair) => (
          <List.Item>
            <Radio value={pair.id}>{pair.label}</Radio>
          </List.Item>
        )}
      />
    </Radio.Group>
  );

  return (
    <Wrapper>
      <CartTools>
        <Search onChange={filterSamples} />
      </CartTools>
      <CartSamplesWrapper className="t-samples-list">
        <Table
          showHeader={false}
          pagination={false}
          bordered={false}
          columns={[
            {
              key: 1,
              title: "BFDSLK",
              render(item, data) {
                const url = setBaseUrl(`/projects/${data.project.id}`);
                return (
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Button type="link" href={`${url}/samples/${data.id}`}>
                      {data.label}
                    </Button>
                    <Button size="small" href={url}>
                      {data.project.label}
                    </Button>
                  </div>
                );
              },
            },
          ]}
          size="small"
          expandable={{
            expandedRowRender(record) {
              return <PairedFileList pairs={record.pairs} />;
            },
          }}
          dataSource={samples.map((p) => ({ key: `project-${p.id}`, ...p }))}
        />

        {/*<Tree checkable treeData={formattedSamples} />*/}
        {/*<AutoSizer>*/}
        {/*  {({ height = 600, width = 400 }) => (*/}
        {/*    <VList*/}
        {/*      itemCount={samples.length}*/}
        {/*      itemSize={75}*/}
        {/*      height={height}*/}
        {/*      width={width}*/}
        {/*    >*/}
        {/*      {renderSample}*/}
        {/*    </VList>*/}
        {/*  )}*/}
        {/*</AutoSizer>*/}
      </CartSamplesWrapper>
      <ButtonsPanelBottom>
        <EmptyCartButton
          className="t-empty-cart-btn"
          type="danger"
          block
          onClick={emptyCart}
        >
          {i18n("cart.clear")}
        </EmptyCartButton>
      </ButtonsPanelBottom>
    </Wrapper>
  );
}

CartSamplesComponent.propTypes = {
  displaySample: PropTypes.func.isRequired,
  removeSample: PropTypes.func.isRequired,
  removeProject: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => ({
  samples: state.cart.filteredSamples,
});

const mapDispatchToProps = (dispatch) => ({
  applyFilter: (filter) => dispatch(actions.applyFilter(filter)),
  emptyCart: () => dispatch(actions.emptyCart()),
  displaySample: (sample) =>
    dispatch(sampleDetailsActions.displaySample(sample)),
  removeSample: (sample) => dispatch(actions.removeSample(sample)),
  removeProject: (id) => dispatch(actions.removeProject(id)),
});

const CartSamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);

export default CartSamples;
