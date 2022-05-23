import React from 'react';
import { Header, Table, List } from 'semantic-ui-react';
import * as wanakana from 'wanakana';

const concatenateReadings = (readings, readingType) => {
  const joined = readings
    .filter((r) => r.readingType === readingType && r.reading.trim())
    .map((r) => (
      <List.Item key={r.id}>
        <span className="tooltip">
          {r.reading}
          <span className="tooltip-text">{wanakana.toRomaji(r.reading)}</span>
        </span>
      </List.Item>
    ));
  return <List horizontal>{joined || 'None'}</List>;
};

const ReadingsTable = ({ readings }) => (
  <React.Fragment>
    <Header as="h3" dividing>
      Readings
    </Header>
    <Table unstackable compact celled textAlign="center" verticalAlign="middle">
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell>On-yomi</Table.HeaderCell>
          <Table.HeaderCell>Kun-yomi</Table.HeaderCell>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        <Table.Row>
          <Table.Cell>{concatenateReadings(readings, 'On')}</Table.Cell>
          <Table.Cell>{concatenateReadings(readings, 'Kun')}</Table.Cell>
        </Table.Row>
      </Table.Body>
    </Table>
  </React.Fragment>
);

export default ReadingsTable;
