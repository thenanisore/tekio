import React from 'react';
import { Link } from 'react-router-dom';
import { Grid, Header, Container } from 'semantic-ui-react';
import { withLayout } from '../common';
import '../styles.css';

const Heading = () => (
  <Container text>
    <Header
      as="h1"
      inverted
      content="Tekiō にようこそ！"
      style={{
        fontSize: '4em',
        fontWeight: 'bold',
        marginBottom: 0,
        marginTop: '3em',
        textAlign: 'center',
      }}
    />
    <Header
      as="h2"
      inverted
      style={{
        fontSize: '1.5em',
        fontWeight: 'normal',
        textAlign: 'center',
      }}>
      The smartest way to learn Japanese. <br />
      Sign up <Link to="/signup">now</Link>!
    </Header>
  </Container>
);

const LandingPage = () => {
  return (
    <div className="landing-view">
      <style>{`
      body > div,
      body > div > div,
      body > div > div > div.landing-view {
        height: 100%;
      };
    `}</style>
      <Grid textAlign="center" verticalAlign="middle">
        <Grid.Column>
          <Heading />
        </Grid.Column>
      </Grid>
    </div>
  );
};

export default withLayout(LandingPage);
