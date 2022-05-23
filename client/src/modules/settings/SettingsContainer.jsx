import _ from 'lodash';
import React from 'react';
import { connect } from 'react-redux';
import Settings from './Settings';
import { putSettingsRequest } from '../../actions/user';

class SettingsContainer extends React.Component {
  constructor({ settings }) {
    super();
    this.state = {
      settings,
    };
  }

  handleSubmit = () => {
    const { applySettings, settings, token } = this.props;
    const newSettings = { ...settings, ...this.state.settings };
    applySettings(newSettings, token);
  };

  handleChange = (key, value) => {
    this.setState({
      settings: { ...this.state.settings, [key]: value },
    });
  };

  render() {
    const { settings, uploading } = this.props;
    return (
      <Settings
        settings={this.state.settings}
        error={this.props.error}
        isUpdated={!_.isEqual(this.state.settings, settings)}
        uploading={uploading}
        onSubmit={this.handleSubmit}
        onChange={this.handleChange}
      />
    );
  }
}

const mapStateToProps = (state) => ({
  token: state.auth.token,
  settings: state.user.settings,
  uploading: state.user.uploading,
  error: state.user.error,
});

const mapDispatchToProps = (dispatch) => ({
  applySettings: (settings, token) =>
    dispatch(putSettingsRequest(settings, token)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(SettingsContainer);
