import React from 'react';
import ReviewQuestion from '../review/ReviewQuestion';

class LessonReview extends React.Component {
  render() {
    const { kanji, autoReveal, onNext, onFinish } = this.props;
    return (
      <ReviewQuestion
        kanji={kanji}
        autoReveal={autoReveal}
        onNext={onNext}
        onFinish={onFinish}
        lesson={true}
      />
    );
  }
}

export default LessonReview;
