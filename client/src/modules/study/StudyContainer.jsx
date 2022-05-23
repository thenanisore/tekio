import _ from 'lodash';
import React from 'react';
import { connect } from 'react-redux';
import { Loader } from 'semantic-ui-react';
import * as study from '../../actions/study';
import NoLessons from './NoLessons';
import StudyStats from './StudyStats';
import Lesson from './Lesson';
import LessonReview from './LessonReview';

class StudyContainer extends React.Component {
  constructor(props) {
    super();
    this.state = this.initializeState(props);
  }

  initializeState = ({ queue, settings }) => {
    const lessonQueue = this.actualizeQueue(queue, settings);
    const lessonReviewQueue = this.getLessonReviews(lessonQueue);
    const now = new Date().getTime();
    console.log(`Lesson started at: ${now}`);
    return {
      lessonQueue, // [1, 2, 3, ...] - kanji ids
      lessonReviewQueue, // [{id: 1, reviewType: 'READING'}, ...]
      seen: [],
      learned: [],
      start: now,
      end: null,
    };
  };

  componentDidMount() {
    this.prepareLessons();
  }

  prepareLessons() {
    const { finished, prepareLessons, token, learned } = this.props;
    if (finished && learned && learned.length > 0) {
      prepareLessons(learned, token);
    } else {
      const { lessonQueue } = this.state;
      if (lessonQueue.length > 0) {
        prepareLessons(lessonQueue, token);
      }
    }
  }

  actualizeQueue = (queue, settings) => {
    if (settings && queue) {
      return queue.slice(0, settings.batchSize);
    } else {
      return [];
    }
  };

  getLessonReviews = (queue) => {
    return _.shuffle(
      queue.flatMap((id) => [
        { id, reviewType: 'Meaning' },
        { id, reviewType: 'Reading' },
      ]),
    );
  };

  handlePreviousLesson = () => {
    const { lessonQueue, seen } = this.state;
    if (seen.length > 0) {
      const prev = seen[seen.length - 1];
      this.setState({
        lessonQueue: [prev].concat(lessonQueue),
        seen: seen.slice(0, seen.length - 1),
      });
    }
  };

  handleNextLesson = () => {
    const { lessonQueue, seen } = this.state;
    if (lessonQueue.length > 0) {
      this.setState({
        lessonQueue: lessonQueue.slice(1),
        seen: seen.concat([lessonQueue[0]]),
      });
    }
  };

  enqueue = (item, queue) => {
    return queue.concat([item]);
  };

  handleNextLessonReview = (answer) => {
    const { lessonReviewQueue: queue, learned } = this.state;
    let updatedQueue = queue.slice(1);
    let updatedLearned = [...learned];
    if (answer.isCorrect) {
      const isLearned =
        updatedQueue.find((i) => i.id === answer.kanjiId) === undefined;
      updatedLearned = isLearned
        ? updatedLearned.concat([answer.kanjiId])
        : updatedLearned;
    } else {
      updatedQueue = this.enqueue(queue[0], updatedQueue);
    }
    this.setState(
      { lessonReviewQueue: updatedQueue, learned: updatedLearned },
      () => {
        if (this.state.lessonReviewQueue.length === 0) {
          this.handleFinish();
        }
      },
    );
  };

  handleFinish = () => {
    const now = new Date().getTime();
    console.log(`Lesson ended at: ${now}`);
    this.setState({ end: now }, () => this.props.finishLessons());
  };

  handleUploadLessons = () => {
    const { uploaded, uploading, uploadLessons, token } = this.props;
    const { learned, start, end } = this.state;
    if (!uploaded && !uploading && learned.length !== 0) {
      const spent = end - start;
      console.log(`Spent time on lesson: ${spent}`);
      uploadLessons(learned, spent, token);
    }
  };

  handleStartStudy = () => {
    this.props.startLessons();
    this.setState(this.initializeState(this.props), () =>
      this.prepareLessons(),
    );
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
      const { finished, kanjiInfo, settings } = this.props;
      const { lessonQueue, lessonReviewQueue, seen } = this.state;
      if (finished) {
        return (
          <StudyStats
            learned={this.props.learned}
            kanjiInfo={this.props.kanjiInfo}
            onStartStudy={this.handleStartStudy}
            onUploadLessons={this.handleUploadLessons}
          />
        );
      } else {
        if (lessonQueue && lessonQueue.length > 0) {
          const currentLesson = kanjiInfo[lessonQueue[0]];
          if (currentLesson) {
            return (
              <Lesson
                kanji={currentLesson}
                seen={seen.length}
                total={lessonQueue.length + seen.length}
                onPrevious={this.handlePreviousLesson}
                onNext={this.handleNextLesson}
              />
            );
          }
        } else if (lessonReviewQueue && lessonReviewQueue.length > 0) {
          const currentLessonReview = kanjiInfo[lessonReviewQueue[0].id];
          const reviewItem = {
            ...currentLessonReview,
            item: { reviewType: lessonReviewQueue[0].reviewType },
          };
          return (
            <LessonReview
              kanji={reviewItem}
              onNext={this.handleNextLessonReview}
              onFinish={this.handleFinish}
              autoReveal={settings.autoReveal}
            />
          );
        }
        return <NoLessons />;
      }
    }
  }
}

const mapStateToProps = (state) => ({
  queue: state.user.lessonQueue,
  token: state.auth.token,
  settings: state.user.settings,
  kanjiInfo: state.study.kanjiInfo,
  loading: state.study.loading,
  finished: state.study.finished,
  learned: state.study.learned,
  error: state.study.error,
  uploaded: state.study.uploaded,
  uploading: state.study.uploading,
});

const mapDispatchToProps = (dispatch) => ({
  prepareLessons: (ids, token) =>
    dispatch(study.prepareLessonsRequest(ids, token)),
  finishLessons: () => dispatch(study.finishLessons()),
  startLessons: () => dispatch(study.startLessons()),
  uploadLessons: (lessons, spent, token) =>
    dispatch(study.uploadLessonsRequest(lessons, spent, token)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(StudyContainer);
