import { connect } from 'react-redux';
import MessageList from '../../components/chat/MessageList';

const mapStateToProps = (state) => {
  return {
    messages: state.chat.messages,
  };
};

const mapDispatchToProps = () => {
  return {};
};

const MessageListContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(MessageList);

export default MessageListContainer;
