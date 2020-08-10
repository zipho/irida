import React, { useEffect, useReducer, useRef } from "react";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { useNavigate } from "@reach/router";
import {
  getClientDetails,
  updateClientDetails,
} from "../../../../apis/clients/clients";
import { Button, Popconfirm, Tabs, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import { WarningAlert } from "../../../../components/alerts";
import { SPACE_SM } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { updateUserGroupDetails } from "../../../../apis/users/groups";

const { Paragraph, Title } = Typography;
const { TabPane } = Tabs;

const reducer = (state, action) => {
  switch (action.type) {
    case "load":
      return { ...state, loading: false, ...action.payload };
    case "update":
      return { ...state, ...action.payload };
    case "tab":
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
};

/**
 * React component to display a page for viewing Client Details.
 * @param {number} id - identifier for the client
 * @returns {*}
 * @constructor
 */
export default function ClientDetailsPage({ id }) {
  const [state, dispatch] = useReducer(reducer, {
    loading: true,
    tab: "members",
  });
  const deleteRef = useRef();

  const navigate = useNavigate();

  useEffect(() => {
    getClientDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );
  }, [id]);

  /**
   * When the value of either the name or description is changes, update the
   * server.
   * @param {string} field to be updated
   * @param {string} value to update to.
   */
  const updateField = (field, value) => {
    updateUserGroupDetails({ id, field, value }).then(() =>
      dispatch({ type: "update", payload: { [field]: value } })
    );
  };

  /**
   * Update contents of the table
   * @returns {void | Promise<void>}
   */
  const updateTable = () =>
    getClientDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );

  const fields = state.loading
    ? []
    : [
      {
        title: i18n("client.clientid"),
        desc: state.canManage ? (
          <Paragraph
            className={"t-group-name"}
            editable={{ onChange: (value) => updateField("clientId", value) }}
          >
            {state.clientId}
          </Paragraph>
        ) : (
          state.clientId
        ),
      },
      {
        title: i18n("UserGroupDetailsPage.description"),
        desc: state.canManage ? (
          <Paragraph
            ellipsis={{ rows: 3, expandable: true }}
            editable={{
              onChange: (value) => updateField("description", value),
            }}
          >
            {state.description || ""}
          </Paragraph>
        ) : (
          <Paragraph ellipsis={{ rows: 3, expandable: true }}>
            state.description
          </Paragraph>
        ),
      },
      {
        title: i18n("UserGroupDetailsPage.members"),
        desc: (
          <UserGroupRolesProvider>
            <UserGroupMembersTable
              updateTable={updateTable}
              members={state.members}
              canManage={state.canManage}
              groupId={id}
            />
          </UserGroupRolesProvider>
        ),
      },
    ];

  const removeClient = () => (
    <div>
      <WarningAlert message={i18n("UserGroupDetailsPage.delete-warning")} />
      <form
        style={{ marginTop: SPACE_SM }}
        ref={deleteRef}
        action={setBaseUrl(`/groups/${id}/delete`)}
        method="POST"
      >
        <Popconfirm
          onConfirm={() => deleteRef.current.submit()}
          title={i18n("UserGroupDetailsPage.delete-confirm")}
          okButtonProps={{ className: "t-delete-confirm-btn" }}
        >
          <Button className="t-delete-group-btn" type="primary" danger>
            {i18n("UserGroupDetailsPage.delete-button")}
          </Button>
        </Popconfirm>
      </form>
    </div>
  );

  return (
    <PageWrapper
      title={"Clients"}
      onBack={() => navigate(setBaseUrl("/admin/clients"), { replace: true })}
    >
      <Tabs
        defaultActiveKey="details"
        tabPosition="left"
        tabBarStyle={{ width: 200 }}
      >
        <TabPane tab={i18n("UserGroupDetailsPage.tab.details")} key="details">
          <Title level={4}>{i18n("UserGroupsDetailsPage.title.details")}</Title>
          <BasicList dataSource={fields} />
        </TabPane>
        <TabPane tab={i18n("UserGroupDetailsPage.tab.projects")} key="project">
          <Title level={4}>
            {i18n("UserGroupsDetailsPage.title.projects")}
          </Title>
        </TabPane>
        {state.canManage ? (
          <TabPane tab={i18n("UserGroupDetailsPage.tab.delete")} key="delete">
            <Title level={4}>
              {i18n("UserGroupsDetailsPage.title.delete")}
            </Title>
            <RemoveClient />
          </TabPane>
        ) : null}
      </Tabs>
    </PageWrapper>
  );
}