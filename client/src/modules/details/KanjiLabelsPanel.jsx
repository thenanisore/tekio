import React from 'react';
import { Label, Table } from 'semantic-ui-react';

const formatJLPTLabel = (JLPT) => {
  return (
    <Label
      basic
      className="kanji-info-label"
      size="medium"
      color="green"
      as="a">
      JLPT
      <Label.Detail>{JLPT}</Label.Detail>
    </Label>
  );
};

const formatGradeLabel = (grade) => {
  return grade === 'G8' ? (
    <Label basic className="kanji-info-label" size="medium" color="blue" as="a">
      High School
    </Label>
  ) : (
    <Label basic className="kanji-info-label" size="medium" color="blue" as="a">
      Grade
      <Label.Detail>{grade.slice(1, 2)}</Label.Detail>
    </Label>
  );
};

const formatStrokeLabel = (strokeCount) => {
  return (
    <Label
      basic
      className="kanji-info-label"
      size="medium"
      color="olive"
      as="a">
      Strokes
      <Label.Detail>{strokeCount}</Label.Detail>
    </Label>
  );
};

const KanjiLabelsPanel = ({ JLPT, grade, strokeCount }) => (
  <Table
    basic="very"
    compact
    textAlign="center"
    verticalAlign="middle"
    unstackable>
    <Table.Body>
      <Table.Row textAlign="center">
        <Table.Cell>{formatJLPTLabel(JLPT)}</Table.Cell>
        <Table.Cell>{formatGradeLabel(grade)}</Table.Cell>
        <Table.Cell>{formatStrokeLabel(strokeCount)}</Table.Cell>
      </Table.Row>
    </Table.Body>
  </Table>
);

export default KanjiLabelsPanel;
