import PropTypes from 'prop-types';
import React from 'react';
import { Loader, Grid, Visibility } from 'semantic-ui-react';
import KanjiItem from './KanjiItem';

class KanjiList extends React.Component {
  constructor() {
    super();
    this.state = {
      page: 1,
      pageCount: 50,
      paginate: false,
    };
  }

  componentWillReceiveProps() {
    this.setState({
      page: 1,
      paginate: false,
    });
  }

  initializePagination = (e) => {
    this.setState({ paginate: true });
  };

  handleNextPage = (e) => {
    const { page, pageCount, paginate } = this.state;
    if (paginate && page * pageCount < this.props.items.length) {
      this.setState({ page: this.state.page + 1, paginate: false });
    }
  };

  render() {
    const { items, loading, onOpenDetails } = this.props;
    const { page, pageCount } = this.state;
    const cards = loading ? (
      <div className="kanji-list-loading">
        <Loader size="massive" inverted active>
          Loading kanji
        </Loader>
      </div>
    ) : (
      items.slice(0, page * pageCount).map((item) => (
        <Grid.Column
          key={item.id}
          mobile={4}
          tablet={4}
          computer={3}
          className="kanji-column">
          <KanjiItem item={item} onOpenDetails={onOpenDetails} />
        </Grid.Column>
      ))
    );

    return (
      <Grid textAlign="center" container>
        <Grid.Row divided>{cards}</Grid.Row>
        <Grid.Row>
          <Visibility
            once={false}
            onOffScreen={this.initializePagination}
            onOnScreen={this.handleNextPage}
          />
        </Grid.Row>
      </Grid>
    );
  }
}

KanjiList.propTypes = {
  items: PropTypes.arrayOf(PropTypes.object).isRequired,
  onOpenDetails: PropTypes.func.isRequired,
};

export default KanjiList;
