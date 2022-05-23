import React from 'react';
import { connect } from 'react-redux';
import { fetchProfileRequest, putProfileRequest } from '../../actions/profile';
import Profile from './Profile';
import UserNotFound from './UserNotFound';

class ProfileContainer extends React.Component {
  componentDidMount() {
    this.initialize(this.props);
  }

  componentWillReceiveProps(props) {
    if (!props.error) {
      this.initialize(props);
    }
  }

  initialize(props) {
    const { profile, loading, fetchProfile, id } = props;
    if ((!profile || profile.id !== id) && !loading) {
      fetchProfile(id);
    }
  }

  handleSubmit = (profile) => {
    const { putProfile, token } = this.props;
    putProfile(profile, token);
  };

  render() {
    const { profile, loading, uploading, uploaded, error, userId } = this.props;
    const own = profile ? userId === profile.id : false;
    if (error && error.code === 404) {
      return <UserNotFound />;
    } else {
      return (
        <Profile
          profile={profile}
          loading={loading}
          uploading={uploading}
          uploaded={uploaded}
          error={error}
          own={own}
          onApply={this.handleSubmit}
        />
      );
    }
  }
}

const mapStateToProps = (state) => ({
  token: state.auth.token,
  userId: state.user.id,
  profile: state.profile.profile,
  loading: state.profile.loading,
  uploading: state.profile.uploading,
  uploaded: state.profile.uploaded,
  error: state.profile.error,
});

const mapDispatchToProps = (dispatch) => ({
  fetchProfile: (id) => dispatch(fetchProfileRequest(id)),
  putProfile: (profile, token) => dispatch(putProfileRequest(profile, token)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ProfileContainer);
