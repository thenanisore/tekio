import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import {
  fetchKanjiFullByLiteralRequest,
  unlockKanjiRequest,
} from '../../actions/kanji';
import { getFullByLiteral } from '../../reducers/kanji';
import KanjiDetails from './KanjiDetails';
import KanjiNotFound from './KanjiNotFound';

class KanjiDetailsContainer extends React.Component {
  componentDidMount() {
    const { literal, item, loading, token } = this.props;
    if (!loading && !item) {
      // item has not been fetched, fetching full
      this.props.fetchFull(literal, token);
    }
  }

  handleUnlock = () => {
    const { item, token, unlock } = this.props;
    unlock(item.id, token);
  };

  render() {
    const { error } = this.props;
    return error && error.code === 404 ? (
      <KanjiNotFound />
    ) : (
      <KanjiDetails
        loading={this.props.loading}
        item={this.props.item}
        isUnlocking={this.props.isUnlocking}
        onUnlock={this.handleUnlock}
      />
    );
  }
}

KanjiDetailsContainer.propTypes = {
  item: PropTypes.object,
  token: PropTypes.string.isRequired,
  literal: PropTypes.string.isRequired,
  loading: PropTypes.bool.isRequired,
  isUnlocking: PropTypes.bool.isRequired,
};

const mapStateToProps = (state, { literal }) => ({
  item: getFullByLiteral(literal, state),
  token: state.auth.token,
  loading: state.kanji.loading,
  error: state.kanji.error,
  isUnlocking: state.kanji.isUnlocking,
});

const mapDispatchToProps = (dispatch) => ({
  fetchFull: (literal, token) =>
    dispatch(fetchKanjiFullByLiteralRequest(literal, token)),
  unlock: (id, token) => dispatch(unlockKanjiRequest(id, token)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(KanjiDetailsContainer);
