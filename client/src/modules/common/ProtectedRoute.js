import PropTypes from 'prop-types';
import React from 'react';
import { Route, Redirect } from 'react-router';
import { connect } from 'react-redux';
import { isAuthenticated } from '../../reducers/auth';
import { fetchUserRequest } from '../../actions/user';
import { logout } from '../../actions/auth';
import { hasToken } from '../../utils/auth';

class ProtectedRoute extends React.Component {
  componentDidMount() {
    const {
      isAuthenticated,
      fetchUser,
      token,
      loadingAuth,
      loadingUser,
      fetched,
      invalidate,
      logout,
      location,
    } = this.props;

    if (isAuthenticated) {
      if ((!fetched || invalidate) && !loadingUser) {
        console.log('Authenticated, fetching user info');
        fetchUser(token);
      }
    } else if (!loadingAuth && !hasToken()) {
      // purge state and redirect to login
      console.log('Cannot authenticate, logging out');
      logout(location.pathname);
    }
  }

  componentWillReceiveProps({ invalidate, errorUser: error }) {
    if (error && error.code === 401) {
      console.log('Unauthorized, purging state');
      this.props.logout();
    } else if (invalidate) {
      console.log('Invalidating user, fetching user info');
      const { fetchUser, token } = this.props;
      fetchUser(token);
    }
  }

  render() {
    const { component: Component, isAuthenticated } = this.props;
    const loading = this.props.loadingUser || this.props.loadingAuth;
    const error = this.props.errorUser || this.props.errorAuth;
    return (
      <Route
        render={() =>
          isAuthenticated ? (
            <Component {...this.props} loading={loading} error={error} />
          ) : (
            <Redirect
              to={{
                pathname: '/login',
                state: { from: this.props.location },
              }}
            />
          )
        }
      />
    );
  }
}

ProtectedRoute.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
  token: PropTypes.string,
  loadingAuth: PropTypes.bool.isRequired,
  loadingUser: PropTypes.bool.isRequired,
  fetched: PropTypes.bool.isRequired,
  invalidate: PropTypes.bool.isRequired,
  location: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  isAuthenticated: isAuthenticated(state),
  token: state.auth.token,
  errorAuth: state.auth.error,
  errorUser: state.user.error,
  loadingAuth: state.auth.loading,
  loadingUser: state.user.loading,
  fetched: state.user.fetched,
  invalidate: state.user.invalidate,
});

const mapDispatchToProps = (dispatch) => ({
  fetchUser: (token) => dispatch(fetchUserRequest(token)),
  logout: (from) => dispatch(logout(true, from)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ProtectedRoute);
