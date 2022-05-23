import React from 'react';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router';
import { tokenReceived } from '../actions/auth';
import { rehydrateReviewsRequest } from '../actions/review';
import { getToken, hasToken } from '../utils/auth';
import { LoginPage, LogoutPage } from './auth';
import { AnonymousRoute, NotFoundPage, ProtectedRoute, Root } from './common';
import { DetailsPage } from './details';
import { DictionaryPage } from './dictionary';
import { LandingPage } from './landing';
import { ProfilePage } from './profile';
import { ReviewPage } from './review';
import { SettingsPage } from './settings';
import { SignupPage } from './signup';
import { StudyPage } from './study';

class App extends React.Component {
  componentDidMount() {
    if (hasToken()) {
      // initial auth
      this.props.authenticateFromStorage(getToken());
      this.props.rehydrateReviews();
    }
  }

  render() {
    return (
      <Switch>
        <Route exact path="/" component={Root} />
        <AnonymousRoute path="/welcome" component={LandingPage} />
        <AnonymousRoute path="/login" component={LoginPage} />
        <AnonymousRoute path="/signup" component={SignupPage} />
        <ProtectedRoute path="/review" component={ReviewPage} />
        <ProtectedRoute path="/study" component={StudyPage} />
        <ProtectedRoute path="/profile/:id" component={ProfilePage} />
        <ProtectedRoute exact path="/kanji" component={DictionaryPage} />
        <ProtectedRoute path="/kanji/:literal" component={DetailsPage} />
        <ProtectedRoute path="/settings" component={SettingsPage} />
        <ProtectedRoute path="/logout" component={LogoutPage} />
        <Route component={NotFoundPage} />
      </Switch>
    );
  }
}

const mapStateToProps = (state) => ({
  reviewsRehydrated: state.review.rehydrated,
});

const mapDispatchToProps = (dispatch) => ({
  authenticateFromStorage: (token) => dispatch(tokenReceived(token)),
  rehydrateReviews: () => dispatch(rehydrateReviewsRequest()),
});

export default withRouter(
  connect(
    mapStateToProps,
    mapDispatchToProps,
  )(App),
);
