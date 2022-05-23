import React from 'react';
import { connect } from 'react-redux';
import { Container, Grid, Header } from 'semantic-ui-react';
import { push } from 'connected-react-router';
import '../styles.css';

const Heading = ({ upperText, text, onReturn }) => (
  <Container text>
    <Header
      as="h3"
      inverted
      style={{
        fontSize: '2em',
        fontWeight: 'bold',
        marginBottom: 0,
        marginTop: '3em',
        textAlign: 'center',
      }}>
      {upperText}
    </Header>
    <Header
      as="h1"
      inverted
      style={{
        fontSize: '4em',
        fontWeight: 'bold',
        marginBottom: 0,
        // marginTop: '3em',
        textAlign: 'center',
      }}>
      {text}
    </Header>
    <Header
      as="h2"
      inverted
      style={{
        fontSize: '1.5em',
        fontWeight: 'normal',
        textAlign: 'center',
      }}>
      <button onClick={onReturn} className="link-button">
        Go back
      </button>
    </Header>
  </Container>
);

class NotFoundComponent extends React.Component {
  handleReturn = (e) => {
    const { history, goHome } = this.props;
    history ? history.goBack() : goHome();
  };

  render() {
    const { upperText, text } = this.props;
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
            <Heading
              upperText={upperText}
              text={text}
              onReturn={this.handleReturn}
            />
          </Grid.Column>
        </Grid>
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch) => ({
  goHome: () => dispatch(push('/')),
});

export default connect(
  null,
  mapDispatchToProps,
)(NotFoundComponent);
