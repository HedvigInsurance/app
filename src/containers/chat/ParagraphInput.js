import { connect } from 'react-redux';
import ParagraphInput from '../../components/chat/ParagraphInput';
import { types } from '../../../hedvig-redux';

const mapStateToProps = (state, ownProps) => {
  return {
    message: state.chat.messages[ownProps.messageIndex],
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    startPolling: (pollingInterval) =>
      dispatch({
        type: types.START_POLLING_MESSAGES,
        payload: { pollingInterval },
      }),
    stopPolling: () =>
      dispatch({ type: types.STOP_POLLING_MESSAGES, payload: {} }),
  };
};

const ParagraphInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(ParagraphInput);

export default ParagraphInputContainer;
