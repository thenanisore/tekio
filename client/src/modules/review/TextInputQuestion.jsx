import _ from 'lodash';
import React from 'react';
import {
  Button,
  Divider,
  Form,
  Grid,
  Header,
  Icon,
  Message,
} from 'semantic-ui-react';

const TextInputQuestion = ({
  input,
  kanji,
  answer,
  correct,
  loading,
  lesson,
  onSubmit,
  onInput,
  onNext,
  onUndo,
  onIgnore,
}) => (
  <React.Fragment>
    <Grid centered>
      <Grid.Row>
        <Grid.Column
          width={6}
          className="kanji-review-left kanji-review-literal">
          <Header as="h1">{kanji.literal}</Header>
        </Grid.Column>
      </Grid.Row>
      <Divider horizontal>{kanji.item.reviewType}</Divider>
      <Grid.Row centered columns="equal">
        <Grid.Column textAlign="center" verticalAlign="middle">
          {lesson ? (
            ''
          ) : (
            <Button
              fluid
              basic
              color="orange"
              onClick={answer ? onIgnore : onUndo}
              className="review-button">
              <Icon name={answer ? 'close' : 'angle double left'} />
              {answer ? 'Ignore' : 'Undo answer'}
            </Button>
          )}
        </Grid.Column>
        <Grid.Column width={8} textAlign="center" verticalAlign="middle">
          {answer ? (
            <Message
              className="review-message"
              size="big"
              color={correct ? 'green' : 'red'}>
              {correct ? _.capitalize(answer) : answer}
            </Message>
          ) : (
            <Form.Input
              fluid
              loading={loading}
              disabled={answer}
              error={correct === false}
              focus
              value={input}
              onChange={onInput}
              className="review-input"
              size="large"
            />
          )}
        </Grid.Column>
        <Grid.Column textAlign="center" verticalAlign="middle">
          <Button
            fluid
            basic
            color={correct === false ? 'red' : 'green'}
            onClick={answer ? onNext : onSubmit}
            className="review-button">
            {answer ? 'Next' : 'Submit'}
            <Icon name={answer ? 'angle double right' : 'angle right'} />
          </Button>
        </Grid.Column>
      </Grid.Row>
    </Grid>
  </React.Fragment>
);

export default TextInputQuestion;
