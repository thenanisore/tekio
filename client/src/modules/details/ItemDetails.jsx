import _ from 'lodash';
import humanize from 'humanize-duration';
import React from 'react';
import { List, Tab } from 'semantic-ui-react';

const formatNextReviewMsg = (date, reviewInMs) => {
  return `${date.toLocaleString()} (in ${humanize(reviewInMs, {
    largest: 2,
    round: true,
  })})`;
};

const formatProgressPanes = (items) => {
  return _.sortBy(items, (i) => i.reviewType).map((i) => {
    const date = new Date(i.nextReview * 1000);
    const reviewInMs = i.nextReview * 1000 - Date.now();
    return {
      menuItem: i.reviewType,
      render: () => (
        <Tab.Pane attached={false}>
          <List bulleted>
            <List.Item>
              <List.Header>Next review:</List.Header>
              {reviewInMs < 0 ? 'Now' : formatNextReviewMsg(date, reviewInMs)}
            </List.Item>
            <List.Item>
              <List.Header>Priority:</List.Header>
              {i.priority}
            </List.Item>
            <List.Item>
              <List.Header>Last SRS interval:</List.Header>
              {humanize(i.interval * 1000)}
            </List.Item>
            <List.Item>
              <List.Header>Finished:</List.Header>
              {i.finished ? 'yes' : 'no'}
            </List.Item>
            <List.Item>
              <List.Header>Frozen:</List.Header>
              {i.frozen ? 'yes' : 'no'}
            </List.Item>
          </List>
        </Tab.Pane>
      ),
    };
  });
};

const ItemDetails = ({ items }) => (
  <Tab
    menu={{ secondary: true, pointing: true }}
    panes={formatProgressPanes(items)}
  />
);

export default ItemDetails;
