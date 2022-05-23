import React from 'react';
import { Button, Icon, Table } from 'semantic-ui-react';

const UnlockButton = ({ unlockedAt, onUnlock, isUnlocked, isUnlocking }) => {
  const date = unlockedAt ? new Date(unlockedAt) : '';
  return (
    <Table
      basic="very"
      compact
      textAlign="center"
      verticalAlign="middle"
      unstackable>
      <Table.Body>
        <Table.Row textAlign="center">
          <Table.Cell>
            {isUnlocked ? (
              <Button disabled>
                Unlocked on {date.toLocaleDateString()} at{' '}
                {date.toLocaleTimeString()}
              </Button>
            ) : (
              <Button
                animated="fade"
                basic
                color="purple"
                loading={isUnlocking}
                onClick={onUnlock}>
                <Button.Content visible>
                  <Icon name="lock" />
                  Unlock
                </Button.Content>
                <Button.Content hidden>
                  <Icon name="unlock" />
                </Button.Content>
              </Button>
            )}
          </Table.Cell>
        </Table.Row>
      </Table.Body>
    </Table>
  );
};

export default UnlockButton;
