import React from 'react';
import {
  Button,
  Container,
  Divider,
  Dropdown,
  Form,
  Grid,
  Header,
  Loader,
  Message,
  Radio,
  Segment,
  Tab,
} from 'semantic-ui-react';

class Settings extends React.Component {
  handleChange = (key) => (e, { value, checked }) => {
    this.props.onChange(key, value || checked);
  };

  rankingOptions = [
    {
      key: 'Aozora',
      text: 'Literature',
      value: 'Aozora',
      content: 'Literature',
      icon: 'book',
    },
    {
      key: 'News',
      text: 'News',
      value: 'News',
      content: 'News',
      icon: 'newspaper',
    },
    {
      key: 'Twitter',
      text: 'Internet',
      value: 'Twitter',
      content: 'Internet',
      icon: 'twitter',
    },
    {
      key: 'Wiki',
      text: 'Science',
      value: 'Wiki',
      content: 'Wiki',
      icon: 'wikipedia w',
    },
  ];

  settingPanes = [
    {
      menuItem: 'Load Balancing',
      render: () => (
        <Tab.Pane attached={false}>
          <Message>
            <Form success>
              <Header>Minutes per day</Header>
              <p>
                The balancing system will balance your review queue based on how
                much time you'd like to spend each day.
              </p>
              <Form.Input
                type="number"
                value={this.props.settings.minutesPerDay}
                onChange={this.handleChange('minutesPerDay')}
              />
            </Form>
          </Message>
          <Message>
            <Form success>
              <Header>Lesson batch size</Header>
              <p>How many items you want to learn in a study session.</p>
              <Form.Input
                type="number"
                value={this.props.settings.batchSize}
                onChange={this.handleChange('batchSize')}
              />
            </Form>
          </Message>
          <Message>
            <Form success>
              <Header>Max reviews</Header>
              <p>The maximum size of the review queue.</p>
              <Form.Input
                type="number"
                value={this.props.settings.maxReviewSize}
                onChange={this.handleChange('maxReviewSize')}
              />
            </Form>
          </Message>
          <Message>
            <Form success>
              <Header>Max lessons</Header>
              <p>The maximum size of the lesson queue.</p>
              <Form.Input
                type="number"
                value={this.props.settings.maxLessonSize}
                onChange={this.handleChange('maxLessonSize')}
              />
            </Form>
          </Message>
        </Tab.Pane>
      ),
    },
    {
      menuItem: 'Kanji order',
      render: () => (
        <Tab.Pane attached={false}>
          <Message>
            <Header>Preferred kanji ranking</Header>
            <p>
              You can get recommended more useful kanji sooner. For example, if
              you're studying Japanese to read literature, prefer the Literature
              ranking.
            </p>
            <Dropdown
              fluid
              options={this.rankingOptions}
              onChange={this.handleChange('preferredOrder')}
              value={this.props.settings.preferredOrder}
              selection
              placeholder="Ranking"
            />
          </Message>
        </Tab.Pane>
      ),
    },
    {
      menuItem: 'Miscellaneous',
      render: () => (
        <Tab.Pane attached={false}>
          <Message>
            <Form success>
              <Header>Auto-reveal</Header>
              <p>
                Automatically open the details after a wrong answer has been
                submitted.
              </p>
              <Radio
                toggle
                name="autoReveal"
                onChange={this.handleChange('autoReveal')}
                checked={this.props.settings.autoReveal}
              />
            </Form>
          </Message>
        </Tab.Pane>
      ),
    },
  ];

  render() {
    const {
      loading,
      uploading,
      settings,
      onSubmit,
      isUpdated,
      error,
    } = this.props;
    if (loading || !settings) {
      return (
        <div className="kanji-list-loading">
          <Loader size="massive" inverted active>
            Loading settings
          </Loader>
        </div>
      );
    } else {
      return (
        <Container className="segment-details">
          <Segment className="main-segment">
            <Divider horizontal>Settings</Divider>
            <Grid columns={2} stackable>
              <Grid.Row>
                <Grid.Column width={16} className="kanji-details-left">
                  <Tab
                    className="settings-tab"
                    menu={{ secondary: true, pointing: true }}
                    panes={this.settingPanes}
                  />
                </Grid.Column>
              </Grid.Row>
              <Grid.Row id="settings-bottom-panel">
                <Button
                  disabled={!isUpdated || uploading}
                  onClick={onSubmit}
                  loading={uploading}
                  basic
                  color={error ? 'red' : 'green'}>
                  Save settings
                </Button>
              </Grid.Row>
            </Grid>
          </Segment>
        </Container>
      );
    }
  }
}

export default Settings;
