import _ from 'lodash';
import React from 'react';
import {
  Button,
  Divider,
  Grid,
  Header,
  Icon,
  Segment,
} from 'semantic-ui-react';

const QuizItem = ({ choice, onClick, itemClass }) => {
  return (
    <Segment
      onClick={() => onClick()}
      className={'quiz-card jump ' + itemClass}>
      <Grid textAlign="center">
        <Grid.Row>
          <div className="quiz-card-literal">{choice}</div>
        </Grid.Row>
      </Grid>
    </Segment>
  );
};

const initialState = {
  selected: null,
};

class Quiz extends React.PureComponent {
  constructor() {
    super();
    this.state = initialState;
  }

  getItemClass = (i) => {
    if (this.props.answer) {
      // mark selected wrong or correct
      if (this.state.selected === i) {
        if (this.props.correct) {
          return 'correct-card';
        } else {
          return 'wrong-card';
        }
      } else {
        const quizData = this.props.kanji.item.quizData;
        if (i === quizData.choices.findIndex((c) => c === quizData.correct)) {
          return 'correct-card';
        } else {
          return '';
        }
      }
    } else {
      // mark selected or nothing
      if (this.state.selected === i) {
        return 'selected-card';
      } else {
        return '';
      }
    }
  };

  handleChoice = (i) => {
    this.setState({ selected: i });
    this.props.onInput(this.props.kanji.item.quizData.choices[i]);
  };

  handleNext = () => {
    this.props.onNext();
    this.setState(initialState);
  };

  handleIgnore = () => {
    this.props.onIgnore();
    this.setState(initialState);
  };

  handleUndo = () => {
    this.props.onUndo();
    this.setState(initialState);
  };

  render() {
    const { kanji, answer, correct, onSubmit } = this.props;
    const quizData = kanji.item.quizData;
    return (
      <React.Fragment>
        <Grid centered>
          <Grid.Row centered>
            <Grid.Column textAlign="center">
              <Header as="h1">{_.capitalize(quizData.question)}</Header>
            </Grid.Column>
          </Grid.Row>
          <Grid.Row>
            <Grid.Column width={3}>
              <QuizItem
                key={0}
                choice={quizData.choices[0]}
                onClick={() => this.handleChoice(0)}
                itemClass={this.getItemClass(0)}
              />
            </Grid.Column>
            <Grid.Column width={3}>
              <QuizItem
                key={1}
                choice={quizData.choices[1]}
                onClick={() => this.handleChoice(1)}
                itemClass={this.getItemClass(1)}
              />
            </Grid.Column>
          </Grid.Row>
          <Grid.Row>
            <Grid.Column width={3}>
              <QuizItem
                key={2}
                choice={quizData.choices[2]}
                onClick={() => this.handleChoice(2)}
                itemClass={this.getItemClass(2)}
              />
            </Grid.Column>
            <Grid.Column width={3}>
              <QuizItem
                key={3}
                choice={quizData.choices[3]}
                onClick={() => this.handleChoice(3)}
                itemClass={this.getItemClass(3)}
              />
            </Grid.Column>
          </Grid.Row>
          <Divider horizontal>{kanji.item.reviewType}</Divider>
          <Grid.Row centered columns="equal">
            <Grid.Column textAlign="center" verticalAlign="middle">
              <Button
                fluid
                basic
                color="orange"
                onClick={answer ? this.handleIgnore : this.handleUndo}
                className="review-button">
                <Icon name={answer ? 'close' : 'angle double left'} />
                {answer ? 'Ignore' : 'Undo answer'}
              </Button>
            </Grid.Column>
            <Grid.Column width={8} textAlign="center" verticalAlign="middle" />
            <Grid.Column textAlign="center" verticalAlign="middle">
              <Button
                fluid
                basic
                color={correct === false ? 'red' : 'green'}
                onClick={answer ? this.handleNext : onSubmit}
                className="review-button">
                {answer ? 'Next' : 'Submit'}
                <Icon name={answer ? 'angle double right' : 'angle right'} />
              </Button>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </React.Fragment>
    );
  }
}

export default Quiz;
