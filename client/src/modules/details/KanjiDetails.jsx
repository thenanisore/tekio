import PropTypes from 'prop-types';
import React from 'react';
import {
  Container,
  Divider,
  Grid,
  Header,
  Loader,
  Segment,
} from 'semantic-ui-react';
import ItemDetails from './ItemDetails';
import KanjiLabelsPanel from './KanjiLabelsPanel';
import LessonPanel from './LessonPanel';
import MeaningsTable from './MeaningsTable';
import RankingsTable from './RankingsTable';
import ReadingsTable from './ReadingsTable';
import UnlockButton from './UnlockButton';

const KanjiDetails = ({ loading, item, onUnlock, isUnlocking, lesson }) => {
  return loading || !item ? (
    <div className="kanji-list-loading">
      <Loader size="massive" inverted active>
        Loading kanji info
      </Loader>
    </div>
  ) : (
    <Container className="segment-details">
      <Segment className="main-segment">
        <Divider horizontal>{item.literal} kanji details</Divider>
        <Grid columns={2} stackable>
          <Grid.Row>
            <Grid.Column
              width={6}
              stretched
              className="kanji-details-left kanji-details-literal">
              <Header as="h1">{item.literal}</Header>
            </Grid.Column>
            <Grid.Column>
              <KanjiLabelsPanel
                JLPT={item.JLPT}
                grade={item.grade}
                strokeCount={item.strokeCount}
              />
              <MeaningsTable meanings={item.meanings} />
              {lesson ? <ReadingsTable readings={item.readings} /> : ''}
            </Grid.Column>
          </Grid.Row>
          <Grid.Row>
            <Grid.Column width={6} className="kanji-details-left">
              {lesson ? (
                ''
              ) : (
                <UnlockButton
                  isUnlocked={item.isUnlocked}
                  unlockedAt={item.unlockedAt}
                  onUnlock={onUnlock}
                  isUnlocking={isUnlocking}
                />
              )}
            </Grid.Column>
            <Grid.Column>
              {lesson ? (
                ''
              ) : (
                <React.Fragment>
                  <ReadingsTable readings={item.readings} />
                  <RankingsTable
                    rankings={item.rankings}
                    frequencies={item.frequencies}
                  />
                </React.Fragment>
              )}
            </Grid.Column>
          </Grid.Row>
          {item.items && item.isUnlocked ? (
            <Divider horizontal>{item.literal} user progress</Divider>
          ) : (
            ''
          )}
          {item.items && item.isUnlocked ? (
            <Grid.Row>
              <Grid.Column width={16} className="kanji-details-left">
                <ItemDetails items={item.items} />
              </Grid.Column>
            </Grid.Row>
          ) : (
            ''
          )}
        </Grid>
        {lesson && lesson !== true ? <LessonPanel {...lesson} /> : ''}
      </Segment>
    </Container>
  );
};

KanjiDetails.propTypes = {
  item: PropTypes.object,
};

export default KanjiDetails;
