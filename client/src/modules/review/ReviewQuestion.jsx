import React from 'react';
import { Container, Header, Icon, Loader, Segment } from 'semantic-ui-react';
import KanjiDetails from '../details/KanjiDetails';
import TextInputQuestion from './TextInputQuestion';
import Quiz from './Quiz';
import Writing from './Writing';
import * as wanakana from 'wanakana';

const getReviewComponent = (props) => {
  switch (props.kanji.item.reviewType) {
    case 'Meaning':
    case 'Reading':
      return <TextInputQuestion {...props} />;
    case 'Quiz':
      return <Quiz {...props} />;
    case 'Writing':
      return <Writing {...props} />;
    default:
      return (
        <div className="kanji-list-loading">
          <Loader size="massive" active>
            Loading item info
          </Loader>
        </div>
      );
  }
};

const initialState = {
  input: '',
  answer: null,
  correct: null,
  details: false,
  startTime: null,
  stopTime: null,
};

class ReviewQuestion extends React.Component {
  constructor() {
    super();
    this.state = initialState;
  }

  resetState() {
    const startTime = new Date().getTime();
    console.log(`Started question at ${startTime}`);
    this.setState({ ...initialState, startTime });
  }

  componentDidMount() {
    this.resetState();
  }

  checkMeaning = (answer, kanji) => {
    return (
      kanji.meanings.find(
        (m) => m.meaning.toLowerCase() === answer.trim().toLowerCase(),
      ) !== undefined
    );
  };

  checkReading = (answer, kanji) => {
    return (
      kanji.readings
        .flatMap((r) => {
          const correct = [r.reading];
          // kun-readings might have a dot indicating okurigana
          let dot = r.reading.indexOf('.');
          if (dot >= 0) {
            correct.push(r.reading.slice(0, dot));
            correct.push(r.reading.slice(0, dot) + r.reading.slice(dot + 1));
          }
          return correct;
        })
        .find((r) => wanakana.toHiragana(r) === wanakana.toHiragana(answer)) !==
      undefined
    );
  };

  checkQuiz = (answer, kanji) => {
    return kanji.item.quizData.correct === answer;
  };

  checkWriting = (answer, kanji) => {
    // TODO: stuff
    return true;
  };

  handleSubmit = () => {
    const { input } = this.state;
    if (input.trim() !== '') {
      const stopTime = new Date().getTime();
      console.log(`Stopped question at ${stopTime}`);
      this.setState({ stopTime });
      const { kanji, autoReveal } = this.props;
      let correct;
      switch (kanji.item.reviewType) {
        case 'Meaning':
          correct = this.checkMeaning(input, kanji);
          break;
        case 'Reading':
          correct = this.checkReading(input, kanji);
          break;
        case 'Quiz':
          correct = this.checkQuiz(input, kanji);
          break;
        case 'Writing':
          correct = this.checkWriting(input, kanji);
          break;
        default:
          correct = false;
      }
      if (!correct && autoReveal) {
        this.handleOpenDetails();
      }
      this.setState({ answer: input, correct });
    }
  };

  convertInput = (input) => {
    if (!input.endsWith('n')) {
      return wanakana.toKana(input);
    } else if (input.endsWith('nn')) {
      return wanakana.toKana(input.slice(0, input.length - 1));
    } else {
      return input;
    }
  };

  handleInput = (e) => {
    let input;
    switch (this.props.kanji.item.reviewType) {
      case 'Reading':
        input = this.convertInput(e.target.value);
        break;
      case 'Meaning':
        input = e.target.value;
        break;
      case 'Quiz':
        input = e;
        break;
      case 'Writing':
        // TODO:
        input = 'haha';
        break;
      default:
        break;
    }
    this.setState({ input });
  };

  handleOpenDetails = (e) => {
    this.setState({
      details: !this.state.details,
    });
  };

  handleFinish = () => {
    this.props.onFinish();
  };

  handleIgnore = () => {
    this.resetState();
  };

  handleUndo = () => {
    this.props.onUndo();
    this.resetState();
  };

  handleNext = () => {
    const { startTime, stopTime, correct, answer } = this.state;
    const answerInfo = {
      userItemId: this.props.kanji.item.id,
      kanjiId: this.props.kanji.id,
      answeredAt: stopTime,
      timeTaken: stopTime - startTime,
      isCorrect: correct,
      answer: answer,
    };
    this.props.onNext(answerInfo);
    this.resetState();
  };

  render() {
    const { kanji, lesson } = this.props;
    const { input, answer, correct, details } = this.state;
    const childProps = {
      input,
      kanji,
      answer,
      correct,
      lesson,
      onSubmit: this.handleSubmit,
      onInput: this.handleInput,
      onUndo: this.handleUndo,
      onIgnore: this.handleIgnore,
      onNext: this.handleNext,
    };
    return (
      <React.Fragment>
        <Container className="segment-details">
          <Segment className="main-segment">
            {getReviewComponent(childProps)}
          </Segment>
        </Container>
        <Container className="review-bottom-container" textAlign="center">
          <Header
            onClick={this.handleOpenDetails}
            inverted
            as="h3"
            icon
            className="jump review-bottom-button">
            <Icon name="list alternate" />
            Reveal details
          </Header>
          <Header
            onClick={this.handleFinish}
            inverted
            as="h3"
            icon
            className="jump review-bottom-button">
            <Icon name="flag checkered" />
            Finish {lesson ? 'lesson' : 'reviews'}
          </Header>
        </Container>
        {details ? KanjiDetails({ loading: false, item: kanji, lesson }) : ''}
      </React.Fragment>
    );
  }
}

export default ReviewQuestion;
