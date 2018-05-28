import { connect } from 'react-redux';
import DateInput from '../../components/chat/DateInput';
import { chatActions } from '../../../hedvig-redux';

const mapStateToProps = (state, ownProps) => {
  let message = state.chat.messages[ownProps.messageIndex];
  return {
    message,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    onChange: (message, value) =>
      dispatch(chatActions.setResponseValue(message, value)),
    send: (message) =>
      dispatch(
        chatActions.sendChatResponse(message, {
          date: message._inputValue,
        }),
      ),
  };
};

const DateInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(DateInput);

export default DateInputContainer;
