import { connect } from 'react-redux';
import Dashboard from '../../components/dashboard/Dashboard';
import { insuranceActions } from '../../../hedvig-redux';

const mapStateToProps = (state) => {
  return {
    insurance: state.insurance,
    categories: state.insurance.categories,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    getInsurance: () => dispatch(insuranceActions.getInsurance()),
  };
};

const DashboardContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Dashboard);

export default DashboardContainer;
