import { push } from 'connected-react-router';
import _ from 'lodash';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import {
  Container,
  Divider,
  Dropdown,
  Grid,
  Header,
  Icon,
  Search,
} from 'semantic-ui-react';
import { fetchKanjiListRequest } from '../../actions/kanji';
import KanjiList from './KanjiList';

const jlptOptions = [
  { key: 'ALL', text: 'All JLPT levels', value: 'ALL' },
  { key: 'N5', text: 'JLPT N5', value: 'N5' },
  { key: 'N4', text: 'JLPT N4', value: 'N4' },
  { key: 'N3', text: 'JLPT N3', value: 'N3' },
  { key: 'N2', text: 'JLPT N2', value: 'N2' },
  { key: 'N1', text: 'JLPT N1', value: 'N1' },
];

const gradeOptions = [
  { key: 'ALL', text: 'All grades', value: 'ALL' },
  { key: 'G1', text: 'Grade 1', value: 'G1' },
  { key: 'G2', text: 'Grade 2', value: 'G2' },
  { key: 'G3', text: 'Grade 3', value: 'G3' },
  { key: 'G4', text: 'Grade 4', value: 'G4' },
  { key: 'G5', text: 'Grade 5', value: 'G5' },
  { key: 'G6', text: 'Grade 6', value: 'G6' },
  { key: 'G8', text: 'High school', value: 'G8' },
];

class KanjiContainer extends React.Component {
  constructor(props) {
    super();
    this.kanjiList = React.createRef();
    const initial = props.fetchedShort ? this.filterItems(props.items) : [];
    this.state = {
      filtered: initial,
      query: '',
      unlockedOnly: false,
      filters: {
        JLPT: 'ALL',
        grade: 'ALL',
      },
      loadingFilters: false,
      pagination: false,
    };
  }

  componentDidMount() {
    const { fetchKanji, fetchedShort, loading } = this.props;
    if (!loading && !fetchedShort) {
      fetchKanji();
    }
  }

  componentWillReceiveProps() {
    this.handleFiltering();
  }

  onOpenDetailsHandler(id) {
    const literal = this.state.filtered.find((i) => i.id === id).literal;
    this.props.goToKanji(literal);
  }

  isMatchingQuery(rawQuery, item) {
    const query = rawQuery.trim().toLowerCase();
    if (query === '') {
      return true;
    } else {
      return (
        item.meaningsShort.toLowerCase().indexOf(query) >= 0 ||
        item.literal.indexOf(query) >= 0 ||
        item.id === query
      );
    }
  }

  isMatchingFilters(filters, item) {
    const isMatchingFilter = (filter, item) => {
      const [filterName, filterValue] = filter;
      return (
        filterValue === 'ALL' ||
        (item[filterName] && item[filterName] === filterValue)
      );
    };
    // if the item matches all of the filters
    // than no unmatching filters will be found
    return (
      Object.entries(filters).find(
        (filter) => !isMatchingFilter(filter, item),
      ) === undefined
    );
  }

  isMatchingUnlocked(unlockedOnly, item) {
    return !unlockedOnly || this.props.unlocked.find((id) => item.id === id);
  }

  filterItems(items, query = '', filters = {}, unlockedOnly = false) {
    const numberifyId = (item) => ({ ...item, id: parseInt(item.id, 10) });
    const filtered = Object.values(items).filter(
      (item) =>
        this.isMatchingQuery(query, item) &&
        this.isMatchingFilters(filters, item) &&
        this.isMatchingUnlocked(unlockedOnly, item),
    );
    const sorted = _.orderBy(filtered.map(numberifyId), ['id']);
    return sorted;
  }

  handleFiltering = _.debounce(() => {
    const { items } = this.props;
    const { query, filters, unlockedOnly } = this.state;
    const filtered = this.filterItems(items, query, filters, unlockedOnly);
    this.setState({
      loading: false,
      filtered: filtered,
    });
  }, 1000);

  handleQueryChange = (e, { value }) => {
    this.setState(
      {
        query: value.trim(),
        loading: true,
      },
      () => this.handleFiltering(),
    );
  };

  handleFilterChange = (key) => (e, { value }) => {
    this.setState(
      {
        filters: { ...this.state.filters, [key]: value },
        loading: true,
      },
      () => this.handleFiltering(),
    );
  };

  handleToggle = () => {
    this.setState(
      {
        unlockedOnly: !this.state.unlockedOnly,
      },
      () => this.handleFiltering(),
    );
  };

  render() {
    const { loading } = this.props;
    return (
      <div>
        <Container textAlign="center">
          <Grid textAlign="center" columns={3}>
            <Grid.Row>
              <Grid.Column
                id="list-panel-left"
                textAlign="justified"
                verticalAlign="middle">
                <div className="ui checked toggle checkbox">
                  <input
                    onClick={this.handleToggle}
                    checked={this.state.unlockedOnly}
                    id="unlocked-toggle"
                    readOnly={true}
                    tabIndex="0"
                    type="radio"
                  />
                  <label id="unlocked-toggle-label" htmlFor="unlocked-toggle">
                    Unlocked
                  </label>
                </div>
              </Grid.Column>
              <Grid.Column textAlign="center" verticalAlign="middle">
                <Search
                  showNoResults={false}
                  placeholder="Kanji or meaning"
                  loading={this.state.loadingFilters}
                  value={this.state.query}
                  ref={this.queryInput}
                  onSearchChange={this.handleQueryChange}
                />
              </Grid.Column>
              <Grid.Column textAlign="right" verticalAlign="middle">
                <Dropdown
                  icon="filter"
                  options={jlptOptions}
                  defaultValue={jlptOptions[0].value}
                  className="kanji-list-filter"
                  onChange={this.handleFilterChange('JLPT')}
                />
                <Dropdown
                  icon="filter"
                  options={gradeOptions}
                  defaultValue={gradeOptions[0].value}
                  className="kanji-list-filter"
                  onChange={this.handleFilterChange('grade')}
                />
              </Grid.Column>
            </Grid.Row>
          </Grid>
        </Container>
        <Divider horizontal inverted>
          <Header as="h4" inverted>
            <Icon name="unordered list" />
            {this.state.filtered.length} kanji
          </Header>
        </Divider>
        <KanjiList
          loading={loading}
          items={this.state.filtered}
          onOpenDetails={(id) => this.onOpenDetailsHandler(id)}
        />
      </div>
    );
  }
}

KanjiContainer.propTypes = {
  items: PropTypes.object.isRequired,
  fetchedShort: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  unlocked: PropTypes.arrayOf(PropTypes.string),
};

const mapStateToProps = (state) => ({
  items: state.kanji.items,
  fetchedShort: state.kanji.fetchedShort,
  loading: state.kanji.loading,
  unlocked: state.user.unlocked,
});

const mapDispatchToProps = (dispatch) => ({
  fetchKanji: () => dispatch(fetchKanjiListRequest()),
  goToKanji: (literal) => dispatch(push('/kanji/' + literal)),
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(KanjiContainer);
