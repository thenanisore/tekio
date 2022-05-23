import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import {
  Message,
  Button,
  Form,
  Grid,
  Header,
  Segment,
} from 'semantic-ui-react';
import '../styles.css';

export default class LoginForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      email: '',
      password: '',
    };
  }

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value });
  };

  onSubmit = (event) => {
    event.preventDefault();
    this.setState({ error: null }, () => {
      const { email, password } = this.state;
      this.props.onSubmit(email, password);
    });
  };

  getMessage() {
    const { error, location } = this.props;
    if (error) {
      return <Message error content={error.info} />;
    } else if (location && location.state && location.state.from) {
      return (
        <Message info content={'You must be authenticated to see this page.'} />
      );
    } else {
      return '';
    }
  }

  render() {
    return (
      <div className="login-form">
        <style>
          {`
            body > div,
            body > div > div,
            body > div > div > div.login-form {
              height: 100%;
            }
          `}
        </style>
        <Grid
          textAlign="center"
          style={{ height: '100%' }}
          verticalAlign="middle">
          <Grid.Column style={{ maxWidth: 400 }}>
            <Grid.Row>
              <Header as="h1" color="black" textAlign="center">
                {' '}
                ログイン
              </Header>
              <Form
                error={this.props.error != null}
                size="small"
                onSubmit={this.onSubmit}>
                <Segment basic>
                  <Form.Input
                    fluid
                    size="small"
                    placeholder="E-mail"
                    type="email"
                    name="email"
                    value={this.state.email}
                    onChange={this.handleChange}
                  />
                  <Form.Input
                    fluid
                    size="small"
                    placeholder="Password"
                    type="password"
                    name="password"
                    onChange={this.handleChange}
                  />
                  {this.getMessage()}
                  <Button
                    loading={this.props.loading}
                    color="purple"
                    fluid
                    size="small">
                    Login
                  </Button>
                </Segment>
              </Form>
            </Grid.Row>
            <Grid.Row>
              Don't have an account? <Link to="/signup">Sign up!</Link>
            </Grid.Row>
            <Grid.Row>
              <Link to="/">To main page</Link>
            </Grid.Row>
          </Grid.Column>
        </Grid>
      </div>
    );
  }
}
