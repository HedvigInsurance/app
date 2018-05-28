import { connect } from 'react-redux';
import { EditMessageButton } from '../../components/Button';
import { chatActions, dialogActions } from '../../../hedvig-redux';

const mapStateToProps = () => {
  return {};
};

const mapDispatchToProps = (dispatch) => {
  return {
    onPress: () =>
      dispatch(
        dialogActions.showDialog({
          title: 'Vill du ändra ditt svar?',
          paragraph: 'Tryck ja för att ändra ditt\nsvar på förra frågan',
          confirmButtonTitle: 'Ja',
          dismissButtonTitle: 'Nej',
          onConfirm: () => dispatch(chatActions.editLastResponse()),
          onDismiss: () => {},
        }),
      ),
  };
};

const EditMessageButtonContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(EditMessageButton);

export default EditMessageButtonContainer;
