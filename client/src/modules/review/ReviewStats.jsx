import _ from 'lodash';
import React from 'react';
import {
  Divider,
  Grid,
  Button,
  Progress,
  Segment,
  Container,
  Label,
  List,
  Header,
  Icon,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';

const ItemPanel = ({ items, itemInfo, title, color }) => {
  const itemList = items.map((i) => [
    i,
    itemInfo[i] ? itemInfo[i].literal : '〇',
    itemInfo[i] ? itemInfo[i].item.reviewType : 'Loading',
  ]);
  return (
    <React.Fragment>
      <Header as="h2" dividing>
        {title}
      </Header>
      <List horizontal>
        {itemList.map((i) => (
          <List.Item key={i[0]}>
            <Link to={'/kanji/' + i[1]}>
              <Label size="big" color={color}>
                {i[1]} {i[2]}
              </Label>
            </Link>
          </List.Item>
        ))}
      </List>
    </React.Fragment>
  );
};

class ReviewStats extends React.Component {
  componentDidMount() {
    this.props.onUploadAnswers();
  }

  getProgressColor = (count, total) => {
    if (total === 0) {
      return 'grey';
    } else {
      const percentage = count / total;
      if (percentage < 0.3) {
        return 'red';
      } else if (percentage < 0.6) {
        return 'yellow';
      } else if (percentage < 0.8) {
        return 'olive';
      } else {
        return 'green';
      }
    }
  };

  render() {
    const { answers, itemInfo, onStartReview } = this.props;
    const correctAnswers = answers.filter((a) => a.isCorrect);
    const items = _.groupBy(answers, (a) => a.userItemId);
    const correctItems = Object.keys(items).filter(
      (k) => items[k].find((item) => item.isCorrect) !== undefined,
    ); // [ userItemIds ]
    const wrongItems = _.difference(Object.keys(items), correctItems);
    return (
      <Container className="segment-details">
        <Segment className="main-segment">
          <Grid centered>
            <Divider horizontal>Review summary</Divider>
            <Grid.Row centered columns="2">
              <Grid.Column stretched textAlign="center" verticalAlign="middle">
                <Header as="h2" dividing>
                  Items
                </Header>
                <Progress
                  color={this.getProgressColor(
                    correctItems.length,
                    Object.keys(items).length,
                  )}
                  total={Object.keys(items).length}
                  value={correctItems.length}>
                  {correctItems.length} correct of {Object.keys(items).length}{' '}
                  total
                </Progress>
              </Grid.Column>
              <Grid.Column stretched textAlign="center" verticalAlign="middle">
                <Header as="h2" dividing>
                  Answers
                </Header>
                <Progress
                  color={this.getProgressColor(
                    correctAnswers.length,
                    answers.length,
                  )}
                  total={answers.length}
                  value={correctAnswers.length}>
                  {correctAnswers.length} correct of {answers.length} total
                </Progress>
              </Grid.Column>
            </Grid.Row>
            <Grid.Row centered columns="equal">
              <Grid.Column stretched textAlign="center" verticalAlign="middle">
                {!_.isEmpty(itemInfo) ? (
                  <ItemPanel
                    items={correctItems}
                    itemInfo={itemInfo}
                    title={'Answered correctly'}
                    color={'green'}
                  />
                ) : (
                  ''
                )}
              </Grid.Column>
            </Grid.Row>
            <Grid.Row centered columns="equal">
              <Grid.Column stretched textAlign="center" verticalAlign="middle">
                {!_.isEmpty(itemInfo) ? (
                  <ItemPanel
                    items={wrongItems}
                    itemInfo={itemInfo}
                    title={'Answered wrong'}
                    color={'red'}
                  />
                ) : (
                  ''
                )}
              </Grid.Column>
            </Grid.Row>
            <Grid.Row centered columns="equal">
              <Grid.Column textAlign="center" verticalAlign="middle" />
              <Grid.Column textAlign="center" verticalAlign="middle">
                <Button
                  fluid
                  basic
                  color="green"
                  onClick={onStartReview}
                  className="review-button">
                  <Icon name="undo" />
                  New reviews
                </Button>
              </Grid.Column>
              <Grid.Column textAlign="center" verticalAlign="middle" />
            </Grid.Row>
          </Grid>
        </Segment>
      </Container>
    );
  }
}

export default ReviewStats;
