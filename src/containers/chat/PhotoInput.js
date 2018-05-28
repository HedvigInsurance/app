import { connect } from 'react-redux';
import PhotoInput from '../../components/chat/PhotoInput';
import { chatActions, uploadActions } from '../../../hedvig-redux';

const mapStateToProps = (state, ownProps) => {
  let message = state.chat.messages[ownProps.messageIndex];
  return {
    message,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    upload: (message, info) =>
      dispatch(
        uploadActions.upload({
          body: info,
          successActionCreator: (url) =>
            chatActions.sendChatResponse(message, {
              type: 'photo_upload',
              text: url,
            }),
        }),
      ),
  };
};

const PhotoInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(PhotoInput);

export default PhotoInputContainer;
