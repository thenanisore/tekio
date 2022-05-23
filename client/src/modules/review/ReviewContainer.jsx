import _ from 'lodash';
import React from 'react';
import { connect } from 'react-redux';
import { Loader } from 'semantic-ui-react';
import {
  prepareReviewRequest,
  startReview,
  submitAnswer,
  undoAnswer,
  finishReview,
  uploadAnswersRequest,
  stopLoading,
} from '../../actions/review';
import NoReviews from './NoReviews';
import ReviewQuestion from './ReviewQuestion';
import ReviewStats from './ReviewStats';
import { persistReviews } from '../../utils/review';

const ItemPriority = {
  New: 4,
  High: 3,
  Mid: 2,
  Low: 1,
};

class ReviewContainer extends React.Component {
  constructor({ queue, answers }) {
    super();
    this.state = {
      currentQueue: queue ? this.actualizeQueue(queue, answers) : [],
      started: true,
    };
  }

  prepareReview() {
    const { prepareReview, finished, answers, token } = this.props;
    if (finished && this.state.started) {
      // fetch info for stats
      const kanjiIds = answers.map((a) => a.kanjiId);
      prepareReview(kanjiIds, token);
    } else {
      // fetch info for session
      this.setState({ started: true }, () => {
        const { currentQueue } = this.state;
        if (currentQueue.length > 0) {
          const kanjiIds = currentQueue.map((i) => i[1]);
          prepareReview(kanjiIds, token);
        }
      });
    }
  }

  componentDidMount() {
    this.prepareReview();
  }

  componentWillReceiveProps({ reviewState }) {
    persistReviews(reviewState);
  }

  actualizeQueue = (queue, answers) => {
    const shuffled = _.shuffle(
      queue.filter(
        (i) => answers.find((a) => a.userItemId === i[0]) === undefined,
      ),
    );
    return shuffled;
  };

  sortQueueByPriority = (queue) => {
    return _.sortBy(queue, [
      (i) => ItemPriority[this.props.itemInfo[i[0]].priority],
    ]);
  };

  enqueue = (item, queue) => {
    return this.sortQueueByPriority(queue.concat([item]));
  };

  handleNext = (answer) => {
    this.props.submitAnswer(answer);
    const { currentQueue } = this.state;
    let updatedQueue = currentQueue.slice(1);
    if (!answer.isCorrect) {
      updatedQueue = this.enqueue(currentQueue[0], updatedQueue);
    }
    this.setState({ currentQueue: updatedQueue }, () => {
      if (this.state.currentQueue.length === 0) {
        this.handleFinish();
      }
    });
  };

  handleUndo = () => {
    const { answers } = this.props;
    if (answers.length > 0) {
      const last = answers[answers.length - 1];
      const previousItem = [last.userItemId, last.kanjiId];
      const undoneQueue = [previousItem].concat(this.state.currentQueue);
      this.setState({ loading: true, currentQueue: undoneQueue }, () => {
        this.prepareReview();
      });
      this.props.undoAnswer();
    }
  };

  handleFinish = () => {
    this.props.finishReview();
  };

  handleUploadAnswers = () => {
    const { uploaded, uploading, uploadAnswers, answers, token } = this.props;
    if (!uploaded && !uploading && answers.length !== 0) {
      uploadAnswers(answers, token);
    }
  };

  handleStartReview = () => {
    this.props.startReview();
    this.setState({ started: false }, () => this.prepareReview());
  };

  render() {
    if (this.props.loading) {
      return (
        <div className="kanji-list-loading">
          <Loader size="massive" inverted active>
            Loading item info
          </Loader>
        </div>
      );
    } else {
      const { finished, itemInfo, answers, settings } = this.props;
      const { currentQueue } = this.state;
      if (finished) {
        return (
          <ReviewStats
            itemInfo={itemInfo}
            answers={answers}
            onStartReview={this.handleStartReview}
            onUploadAnswers={this.handleUploadAnswers}
          />
        );
      } else {
        if (currentQueue && currentQueue.length > 0) {
          const currentItem = itemInfo[currentQueue[0][0]];
          if (currentItem) {
            return (
              <ReviewQuestion
                kanji={currentItem}
                onSubmitAnswer={this.submitAnswer}
                onNext={this.handleNext}
                onFinish={this.handleFinish}
                onUndo={this.handleUndo}
                autoReveal={settings.autoReveal}
              />
            );
          }
        }
        return <NoReviews />;
      }
    }
  }
}

const mapStateToProps = (state) => ({
  queue: state.user.reviewQueue,
  token: state.auth.token,
  finished: state.review.finished,
  answers: state.review.answers,
  itemInfo: state.review.itemInfo,
  loading: state.review.loading,
  error: state.review.error,
  uploaded: state.review.uploaded,
  uploading: state.review.uploading,
  settings: state.user.settings,
  reviewState: state.review,
});

const mapDispatchToProps = (dispatch) => ({
  prepareReview: (ids, token) => dispatch(prepareReviewRequest(ids, token)),
  submitAnswer: (answer) => dispatch(submitAnswer(answer)),
  undoAnswer: () => dispatch(undoAnswer()),
  finishReview: () => dispatch(finishReview()),
  startReview: () => dispatch(startReview()),
  uploadAnswers: (answers, token) =>
    dispatch(uploadAnswersRequest(answers, token)),
  stopLoading: () => dispatch(stopLoading()),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ReviewContainer);
