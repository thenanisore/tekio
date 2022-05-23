import React from 'react';
import { withLayout } from '../common';
import ProfileContainer from './ProfileContainer';

const ProfilePage = ({ computedMatch }) => {
  return <ProfileContainer id={computedMatch.params.id} />;
};

export default withLayout(ProfilePage);
