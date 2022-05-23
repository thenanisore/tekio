import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import SignupForm from './SignupForm';
import { signupRequest } from '../../actions/signup';

const SignupPage = (props) => <SignupForm {...props} />;

SignupPage.propTypes = {
  successful: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  error: PropTypes.object,
};

const mapStateToProps = (state) => ({
  successful: state.signup.successful,
  loading: state.signup.loading,
  error: state.signup.error,
});

const mapDispatchToProps = (dispatch) => ({
  onSubmit: (email, password, username) =>
    dispatch(signupRequest({ email, password, username })),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(SignupPage);
