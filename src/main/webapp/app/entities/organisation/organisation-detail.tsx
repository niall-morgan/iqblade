import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './organisation.reducer';

export interface IOrganisationDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const OrganisationDetail = (props: IOrganisationDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { organisationEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="organisationDetailsHeading">
          <Translate contentKey="iqBladeApp.organisation.detail.title">Organisation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.id}</dd>
          <dt>
            <span id="companyNumber">
              <Translate contentKey="iqBladeApp.organisation.companyNumber">Company Number</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.companyNumber}</dd>
          <dt>
            <span id="companyName">
              <Translate contentKey="iqBladeApp.organisation.companyName">Company Name</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.companyName}</dd>
          <dt>
            <span id="website">
              <Translate contentKey="iqBladeApp.organisation.website">Website</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.website}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="iqBladeApp.organisation.status">Status</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.status}</dd>
        </dl>
        <Button tag={Link} to="/organisation" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/organisation/${organisationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ organisation }: IRootState) => ({
  organisationEntity: organisation.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(OrganisationDetail);
