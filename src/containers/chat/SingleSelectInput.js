import { connect } from 'react-redux';
import { NavigationActions } from 'react-navigation';

import SingleSelectInput from '../../components/chat/SingleSelectInput';
import { chatActions } from '../../../hedvig-redux';
import { showDashboardAction } from '../../actions/baseNavigation';

const mapStateToProps = (state, ownProps) => {
  return {
    message: state.chat.messages[ownProps.messageIndex],
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    selectChoice: (message, choice) =>
      dispatch(chatActions.selectChoice(message, choice)),
    done: (message) => dispatch(chatActions.sendChatResponse(message)),
    goToDashboard: () => dispatch(showDashboardAction()),
    startTrustly: (id) =>
      dispatch(
        NavigationActions.navigate({
          routeName: 'Payment',
          params: { id },
        }),
      ),
  };
};

const SingleSelectInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(SingleSelectInput);

export default SingleSelectInputContainer;
