import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { Container, Dimmer, Loader } from 'semantic-ui-react';
import { Footer, Header } from '.';
import { isAuthenticated } from '../../reducers/auth';
import { getStaticFile } from '../../utils/api';
import '../styles.css';

class Layout extends React.Component {
  render() {
    return (
      <Dimmer.Dimmable dimmed={this.props.loading}>
        <Dimmer active={this.props.loading}>
          <Loader size="massive" active>
            {this.props.error ? this.props.error.info : 'Fetching user data'}
          </Loader>
        </Dimmer>
        <div className="main-background">
          <div className="main-background-image">
            <div className="main">
              {this.props.onlyBody ? '' : <Header {...this.props} />}
              <div className="main-content">
                {this.props.loading ? (
                  ''
                ) : (
                  <Container>
                    {React.cloneElement(this.props.children, { ...this.props })}
                  </Container>
                )}
              </div>
              {this.props.onlyBody ? '' : <Footer />}
            </div>
          </div>
        </div>
      </Dimmer.Dimmable>
    );
  }
}

Layout.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
  username: PropTypes.string,
  userId: PropTypes.string,
  loading: PropTypes.bool,
};

const defaultPicture = 'noimage.png';

const getUserPicture = (state) => {
  return state.user.picture
    ? getStaticFile(state.user.picture)
    : getStaticFile(defaultPicture);
};

// TODO: count kanji, not items
const getCurrentReviewQueue = (state) => {
  if (state.user.reviewQueue) {
    let answered;
    if (state.review.finished && state.review.uploaded) {
      answered = 0;
    } else {
      answered = state.review.answers.filter((a) => a.isCorrect).length;
    }
    return state.user.reviewQueue.length - answered;
  } else {
    return 0;
  }
};

const getCurrentLessonQueue = (state) => {
  return state.user.lessonQueue ? state.user.lessonQueue.length : 0;
};

const mapStateToProps = (state) => ({
  isAuthenticated: isAuthenticated(state),
  username: state.user.username,
  picture: getUserPicture(state),
  userId: state.user.id,
  lessonCount: getCurrentLessonQueue(state),
  reviewCount: getCurrentReviewQueue(state),
});

const withLayout = (Inner, onlyBody = false, innerProps = {}) => {
  const wrapped = (props) => (
    <Layout {...props} onlyBody={onlyBody}>
      <Inner {...innerProps} />
    </Layout>
  );
  return connect(
    mapStateToProps,
    null,
  )(wrapped);
};

export default withLayout;
