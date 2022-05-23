import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { Route, Redirect } from 'react-router';
import { isAuthenticated } from '../../reducers/auth';

const AnonymousRoute = (props) => {
  const { component: Component, isAuthenticated, location } = props;
  const redirectTo =
    location.state && location.state.from ? location.state.from : '/';
  const comp = isAuthenticated ? (
    <Redirect to={redirectTo} />
  ) : (
    <Component {...props} />
  );
  return <Route render={() => comp} />;
};

AnonymousRoute.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
};

const mapStateToProps = (state) => ({
  isAuthenticated: isAuthenticated(state),
});

export default connect(
  mapStateToProps,
  null,
)(AnonymousRoute);
