import { connect } from 'react-redux';
import ChatNumberInput from '../../components/chat/ChatNumberInput';
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
          text: message._inputValue,
        }),
      ),
  };
};

const ChatNumberInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(ChatNumberInput);

export default ChatNumberInputContainer;
