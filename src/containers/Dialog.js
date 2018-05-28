import { connect } from 'react-redux';
import { dialogActions } from '../../hedvig-redux';
import Dialog from '../components/Dialog';

const mapStateToProps = (state) => {
  return {
    message: state.dialog.message,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    emptyDialog: () => dispatch(dialogActions.emptyDialog()),
    confirmButtonPressed: () => dispatch(dialogActions.confirmButtonPressed()),
    dismissButtonPressed: () => dispatch(dialogActions.dismissButtonPressed()),
  };
};

const DialogContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Dialog);

export default DialogContainer;
