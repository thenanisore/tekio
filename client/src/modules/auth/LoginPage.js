import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import LoginForm from './LoginForm';
import { loginRequest } from '../../actions/auth';
import { isAuthenticated } from '../../reducers/auth';

const LoginPage = (props) => <LoginForm {...props} />;

LoginPage.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
  error: PropTypes.object,
  loading: PropTypes.bool.isRequired,
};

const mapStateToProps = (state) => ({
  isAuthenticated: isAuthenticated(state),
  error: state.auth.error,
  loading: state.auth.loading,
});

const mapDispatchToProps = (dispatch) => ({
  onSubmit: (email, password) => dispatch(loginRequest({ email, password })),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(LoginPage);
