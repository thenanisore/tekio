import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';

const Root = ({ token, id }) =>
  token ? <Redirect to={'/profile/' + id} /> : <Redirect to="/welcome" />;

Root.propTypes = {
  token: PropTypes.string,
};

const mapStateToProps = (state) => ({
  id: state.auth.id,
  token: state.auth.token,
});

export default connect(
  mapStateToProps,
  null,
)(Root);
