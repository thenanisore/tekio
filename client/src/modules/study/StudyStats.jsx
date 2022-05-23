import _ from 'lodash';
import React from 'react';
import {
  Divider,
  Grid,
  Button,
  Segment,
  Container,
  Label,
  List,
  Header,
  Icon,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';

const KanjiPanel = ({ kanji, kanjiInfo }) => {
  console.log(JSON.stringify(kanji));
  console.log(JSON.stringify(kanjiInfo));
  const itemList = kanji.map((i) => [i, kanjiInfo[i].literal]);
  return (
    <React.Fragment>
      <Header as="h2">Unlocked {kanji.length} kanji</Header>
      <List horizontal>
        {itemList.map((i) => (
          <List.Item key={i[0]}>
            <Link to={'/kanji/' + i[1]}>
              <Label size="big" color="violet">
                {i[1]}
              </Label>
            </Link>
          </List.Item>
        ))}
      </List>
    </React.Fragment>
  );
};

class StudyStats extends React.Component {
  componentDidMount() {
    this.props.onUploadLessons();
  }

  render() {
    const { learned, kanjiInfo, onStartStudy } = this.props;
    return (
      <Container className="segment-details">
        <Segment className="main-segment">
          <Grid centered>
            <Divider horizontal>Lesson summary</Divider>
            <Grid.Row centered columns="equal">
              <Grid.Column stretched textAlign="center" verticalAlign="middle">
                {!_.isEmpty(kanjiInfo) ? (
                  <KanjiPanel kanji={learned} kanjiInfo={kanjiInfo} />
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
                  onClick={onStartStudy}
                  className="review-button">
                  <Icon name="undo" />
                  New lessons
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

export default StudyStats;
