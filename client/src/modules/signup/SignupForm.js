import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import {
  Header,
  Segment,
  Button,
  Form,
  Grid,
  Message,
} from 'semantic-ui-react';

export default class SignupForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      validateError: null,
    };
  }

  isValid() {
    return this.state.validateError === null;
  }

  validate() {
    const { email, password, confirmPassword } = this.state;
    if (email === '' || password === '' || confirmPassword === '') {
      this.setState({ validateError: 'Please enter all required fields.' });
      return false;
    }

    if (password !== confirmPassword) {
      this.setState({ validateError: 'Password do not match.' });
      return false;
    }

    return true;
  }

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value, validateError: null });
  };

  onSubmit = (event) => {
    event.preventDefault();
    this.setState({ error: null }, () => {
      if (this.validate()) {
        const { email, password, username } = this.state;
        this.props.onSubmit(email, password, username);
      }
    });
  };

  getMessage() {
    if (this.state.validateError) {
      let msg = this.state.validateError;
      return <Message error content={msg} />;
    }
    if (this.props.error) {
      let msg = this.props.error.info;
      return <Message error content={msg} />;
    }
    if (this.props.successful) {
      let msg = 'Successfully signed up!';
      return <Message success content={msg} />;
    }
    return '';
  }

  render() {
    return (
      <div className="signup-form">
        <style>
          {`
            body > div,
            body > div > div,
            body > div > div > div.signup-form {
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
                サインアップ
              </Header>
              <Form
                error={this.props.error || this.state.validateError}
                size="small"
                onSubmit={this.onSubmit}>
                <Segment basic>
                  <Form.Input
                    fluid
                    size="small"
                    placeholder="E-mail"
                    type="email"
                    name="email"
                    onChange={this.handleChange}
                  />
                  <Form.Input
                    fluid
                    size="small"
                    placeholder="Username"
                    type="text"
                    name="username"
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
                  <Form.Input
                    fluid
                    size="small"
                    placeholder="Confirm password"
                    type="password"
                    name="confirmPassword"
                    onChange={this.handleChange}
                  />
                  {this.getMessage()}
                  <Button
                    loading={this.props.loading}
                    color="purple"
                    fluid
                    size="small">
                    Sign Up
                  </Button>
                </Segment>
              </Form>
            </Grid.Row>
            <Grid.Row>
              Already have an account? <Link to="/login">Sign In!</Link>
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
