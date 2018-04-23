import React from "react";
import { LoaderContainer } from "../Loader";
import { TableContainer } from "../Table";

export const LineList = props => {
  if (props.loading) {
    // LOADING STATE
    return <LoaderContainer />;
  } else if (props.error) {
    // ERROR STATE
    // TODO: (Josh | 2018-04-11) Create error component
    return <h3>A major error has occurred! Better find a bomb shelter!</h3>;
  } else {
    // CREATE TABLE
    return <TableContainer />;
  }
};
