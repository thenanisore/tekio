import React from 'react';
import { Progress, Button, Divider, Grid, Icon } from 'semantic-ui-react';

const LessonPanel = ({ onPrevious, onNext, seen, total }) => {
  const isFirst = seen === 0;
  const isLast = seen === total - 1;
  return (
    <Grid centered>
      <Divider horizontal>Lesson progress</Divider>
      <Grid.Row centered columns="equal">
        <Grid.Column textAlign="center" verticalAlign="middle">
          <Button
            fluid
            basic
            disabled={isFirst}
            color="orange"
            onClick={onPrevious}
            className="review-button">
            <Icon name="angle double left" />
            Previous
          </Button>
        </Grid.Column>
        <Grid.Column width={8} textAlign="center" verticalAlign="middle">
          <Progress
            color={isLast ? 'green' : 'violet'}
            total={total}
            value={seen + 1}>
            {seen + 1} of {total}
          </Progress>
        </Grid.Column>
        <Grid.Column textAlign="center" verticalAlign="middle">
          <Button
            fluid
            basic
            color="green"
            onClick={onNext}
            className="review-button">
            {isLast ? 'Start' : 'Next'}
            <Icon name="angle double right" />
          </Button>
        </Grid.Column>
      </Grid.Row>
    </Grid>
  );
};

export default LessonPanel;
