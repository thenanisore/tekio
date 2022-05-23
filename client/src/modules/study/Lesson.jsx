import React from 'react';
import KanjiDetails from '../details/KanjiDetails';

class Lesson extends React.Component {
  handlePrevious = () => {
    this.props.onPrevious();
  };

  handleNext = () => {
    this.props.onNext();
  };

  render() {
    const { kanji, onPrevious, onNext, seen, total } = this.props;
    const lessonProps = {
      onPrevious,
      onNext,
      seen,
      total,
    };
    return <KanjiDetails item={kanji} lesson={lessonProps} />;
  }
}

Lesson.propTypes = {};

export default Lesson;
