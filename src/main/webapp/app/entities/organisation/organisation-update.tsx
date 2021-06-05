import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Label, Row } from 'reactstrap';
import { AvField, AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { Translate } from '../../config/language';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { createEntity, getEntity, reset, updateEntity } from './organisation.reducer';

export interface IOrganisationUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const OrganisationUpdate = (props: IOrganisationUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { organisationEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/organisation' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...organisationEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="iqBladeApp.organisation.home.createOrEditLabel" data-cy="OrganisationCreateUpdateHeading">
            <Translate contentKey="iqBladeApp.organisation.home.createOrEditLabel">Create or edit a Organisation</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : organisationEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="organisation-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="organisation-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="companyNumberLabel" for="organisation-companyNumber">
                  <Translate contentKey="iqBladeApp.organisation.companyNumber">Company Number</Translate>
                </Label>
                <AvField id="organisation-companyNumber" data-cy="companyNumber" type="text" name="companyNumber" />
              </AvGroup>
              <AvGroup>
                <Label id="companyNameLabel" for="organisation-companyName">
                  <Translate contentKey="iqBladeApp.organisation.companyName">Company Name</Translate>
                </Label>
                <AvField id="organisation-companyName" data-cy="companyName" type="text" name="companyName" />
              </AvGroup>
              <AvGroup>
                <Label id="websiteLabel" for="organisation-website">
                  <Translate contentKey="iqBladeApp.organisation.website">Website</Translate>
                </Label>
                <AvField id="organisation-website" data-cy="website" type="text" name="website" />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="organisation-status">
                  <Translate contentKey="iqBladeApp.organisation.status">Status</Translate>
                </Label>
                <AvField id="organisation-status" data-cy="status" type="text" name="status" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/organisation" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  organisationEntity: storeState.organisation.entity,
  loading: storeState.organisation.loading,
  updating: storeState.organisation.updating,
  updateSuccess: storeState.organisation.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(OrganisationUpdate);
