import { connect } from 'react-redux';
import LoadingIndicator from '../../components/chat/LoadingIndicator';

const mapStateToProps = (state, ownProps) => {
  if (state.chat.messages.length > 0) {
    let message = state.chat.messages[ownProps.messageIndex];
    return {
      loadingMessages: state.chat.loadingMessages,
      avatar: state.chat.avatars[message.header.loadingIndicator] || {},
    };
  } else {
    return {
      avatar: {},
    };
  }
};

const mapDispatchToProps = () => {
  return {};
};

const LoadingIndicatorContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(LoadingIndicator);

export default LoadingIndicatorContainer;
