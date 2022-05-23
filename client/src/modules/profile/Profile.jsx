import _ from 'lodash';
import React from 'react';
import {
  Button,
  Container,
  Divider,
  Form,
  Grid,
  Header,
  Image,
  List,
  Loader,
  Message,
  Progress,
  Segment,
} from 'semantic-ui-react';
import { getStaticFile } from '../../utils/api';

class Profile extends React.Component {
  constructor({ profile }) {
    super();
    this.state = {
      currentProfile: profile || {},
      changing: false,
    };
  }

  componentWillReceiveProps({ uploaded }) {
    if (uploaded) {
      this.setState({ changing: false });
    }
  }

  formatName = (profile) => {
    const name = profile.fullName ? profile.fullName : profile.username;
    return _.capitalize(name);
  };

  formatBirthday = (profile) => {
    return profile.birthDate ? profile.birthDate : 'no info';
  };

  formatBio = (profile) => {
    return profile.bio ? profile.bio : 'none';
  };

  handleChanging = () => {
    this.setState({ changing: !this.state.changing });
  };

  handleChange = (key) => (e, { value }) => {
    this.setState({
      currentProfile: { ...this.state.currentProfile, [key]: value },
    });
  };

  handleApply = () => {
    this.props.onApply(this.state.currentProfile);
  };

  render() {
    const { profile, loading, own, uploading, error } = this.props;
    const { changing, currentProfile } = this.state;
    if (loading || !profile) {
      return (
        <div className="kanji-list-loading">
          <Loader size="massive" inverted active>
            Loading profile
          </Loader>
        </div>
      );
    } else {
      const picURL = profile.picture
        ? getStaticFile(profile.picture)
        : getStaticFile('noimage.png');
      return (
        <Container className="segment-details">
          <Segment className="main-segment">
            <Grid columns={2} stackable>
              <Divider horizontal>{profile.username}'s profile</Divider>
              <Grid.Row>
                <Grid.Column
                  mobile={3}
                  tablet={6}
                  computer={6}
                  className="kanji-details-left">
                  <Image className="user-avatar-big" circular src={picURL} />
                </Grid.Column>
                <Grid.Column>
                  <Header as="h3" dividing textAlign="center">
                    {this.formatName(profile)}
                  </Header>
                  <List>
                    <List.Item>
                      <List.Icon name="birthday cake" />
                      <List.Content>
                        <b>Birthday:</b> {this.formatBirthday(profile)}
                      </List.Content>
                    </List.Item>
                    <List.Item>
                      <List.Icon name="time" />
                      <List.Content>
                        Studying since <b>{profile.joined}</b>
                      </List.Content>
                    </List.Item>
                    <Header>{changing ? 'Edit profile' : 'About'}:</Header>
                    {changing ? (
                      <Message floating>
                        <Form error={error} loading={uploading}>
                          <Form.Field>
                            <label>Username</label>
                            <Form.Input
                              onChange={this.handleChange('username')}
                              value={currentProfile.username}
                              placeholder="Username"
                            />
                          </Form.Field>
                          <Form.Field>
                            <label>Full Name</label>
                            <Form.Input
                              onChange={this.handleChange('fullName')}
                              value={currentProfile.fullName || ''}
                              placeholder="Full name"
                            />
                          </Form.Field>
                          <Form.Field>
                            <label>Birth Date</label>
                            <Form.Input
                              onChange={this.handleChange('birthDate')}
                              value={currentProfile.birthDate || ''}
                              type="date"
                            />
                          </Form.Field>
                          <Form.TextArea
                            label="Bio"
                            onChange={this.handleChange('bio')}
                            defaultValue={currentProfile.bio || ''}
                          />
                          <Message error>
                            Cannot update profile: {error && error.info}
                          </Message>
                          <Form.Group>
                            <Form.Button
                              color="red"
                              onClick={this.handleChanging}>
                              Cancel
                            </Form.Button>
                            <Form.Button
                              color="green"
                              onClick={this.handleApply}>
                              Apply
                            </Form.Button>
                          </Form.Group>
                        </Form>
                      </Message>
                    ) : (
                      <Message floating>{this.formatBio(profile)}</Message>
                    )}
                    {own && !changing ? (
                      <Button
                        style={{ float: 'right' }}
                        onClick={this.handleChanging}>
                        Edit profile
                      </Button>
                    ) : (
                      ''
                    )}
                  </List>
                </Grid.Column>
              </Grid.Row>
              <Divider horizontal>Kanji</Divider>
              <Grid.Row centered>
                <Grid.Column width={10}>
                  <Progress
                    color="green"
                    indicating
                    size="large"
                    percent={profile.unlocked / profile.unlockedOf}>
                    {profile.unlocked} of {profile.unlockedOf} unlocked
                  </Progress>
                </Grid.Column>
              </Grid.Row>
              {/* {own ? (
                <React.Fragment>
                  <Divider horizontal>Stats</Divider>
                  <Grid.Row centered>
                    <Grid.Column width={10}>No stats</Grid.Column>
                  </Grid.Row>
                </React.Fragment>
              ) : (
                ''
              )} */}
            </Grid>
          </Segment>
        </Container>
      );
    }
  }
}

export default Profile;
