import _ from 'lodash';
import React from 'react';
import { Accordion, Header, List } from 'semantic-ui-react';

const MeaningsTable = ({ meanings }) => {
  const primary = meanings.slice(0, 3);
  const others = meanings.slice(3);
  const othersPanel = {
    key: 'others',
    title: {
      content: 'Other meanings',
      icon: 'list',
    },
    content: {
      content: (
        <List bulleted size="large">
          {others.map((m) => {
            return <List.Item key={m.id}>{_.capitalize(m.meaning)}</List.Item>;
          })}
        </List>
      ),
    },
  };
  return (
    <React.Fragment>
      <Header as="h3" dividing>
        Meanings
      </Header>
      <List bulleted size="large">
        {primary.map((m) => {
          return (
            <List.Item key={m.id}>
              <strong>{_.capitalize(m.meaning)}</strong>
            </List.Item>
          );
        })}
      </List>
      {others.length > 0 ? <Accordion panels={[othersPanel]} /> : ''}
    </React.Fragment>
  );
};

export default MeaningsTable;
