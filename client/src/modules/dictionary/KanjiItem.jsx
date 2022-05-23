import _ from 'lodash';
import PropTypes from 'prop-types';
import React from 'react';
import { Grid, Segment, Divider } from 'semantic-ui-react';

const KanjiItem = ({ item, onOpenDetails }) => {
  const note = item.JLPT ? `JLPT ${item.JLPT}` : `JLPT N1+`;
  return (
    <Segment onClick={() => onOpenDetails(item.id)} className="kanji-card jump">
      <Grid textAlign="center">
        <Grid.Row>
          <div className="kanji-card-literal">{item.literal}</div>
        </Grid.Row>
        <Divider />
        <Grid.Row className="kanji-card-description">
          <h4>{_.capitalize(item.meaningsShort)}</h4>
        </Grid.Row>
        <Grid.Row className="kanji-card-notes">
          <h4>{note}</h4>
        </Grid.Row>
      </Grid>
    </Segment>
  );
};

KanjiItem.propTypes = {
  item: PropTypes.object.isRequired,
  onOpenDetails: PropTypes.func.isRequired,
};

export default KanjiItem;
