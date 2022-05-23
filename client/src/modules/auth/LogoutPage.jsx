import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import * as auth from '../../actions/auth';

class LogoutPage extends React.Component {
  componentWillMount() {
    this.props.dispatch(auth.logout());
  }

  render() {
    return 'Signing out...';
  }
}
LogoutPage.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

export default connect()(LogoutPage);
