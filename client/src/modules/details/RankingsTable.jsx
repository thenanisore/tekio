import _ from 'lodash';
import React from 'react';
import { Header, Table } from 'semantic-ui-react';

const formatRanking = (rank) => {
  return rank ? rank + 'th' : 'rare';
};

const formatFrequency = (freq) => {
  if (freq) {
    const percent = freq * 100;
    if (percent < 0.001) {
      return '<0.001%';
    } else {
      return percent.toFixed(3) + '%';
    }
  } else {
    return 'rare';
  }
};

const RankingsTable = ({ rankings, frequencies }) => (
  <React.Fragment>
    <Header as="h3" dividing>
      Frequencies
    </Header>
    <Table unstackable celled textAlign="center" verticalAlign="middle">
      <Table.Header>
        <Table.Row>
          {Object.keys(rankings).map((k) => (
            <Table.HeaderCell key={k}>{_.capitalize(k)}</Table.HeaderCell>
          ))}
        </Table.Row>
      </Table.Header>
      <Table.Body>
        <Table.Row>
          {Object.keys(rankings).map((k) => (
            <Table.Cell key={k}>{formatRanking(rankings[k])}</Table.Cell>
          ))}
        </Table.Row>
        <Table.Row>
          {Object.keys(rankings).map((k) => (
            <Table.Cell key={k}>{formatFrequency(frequencies[k])}</Table.Cell>
          ))}
        </Table.Row>
      </Table.Body>
    </Table>
  </React.Fragment>
);

export default RankingsTable;
