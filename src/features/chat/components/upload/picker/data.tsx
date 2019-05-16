import * as React from 'react';
import { CameraRoll, GetPhotosReturnType, Platform } from 'react-native';
import { Container, ActionMap } from 'constate';
import { Mount } from 'react-lifecycle-components';
import Permissions from 'react-native-permissions';

interface State {
  photos?: Partial<GetPhotosReturnType>;
  error: boolean;
  loading: boolean;
}

interface Actions {
  setPhotos: (photos: GetPhotosReturnType) => void;
  setError: (error: boolean) => void;
  setLoading: (loading: boolean) => void;
}

const actions: ActionMap<State, Actions> = {
  setPhotos: (photos) => () => ({
    photos,
  }),
  setError: (error) => () => ({
    error,
  }),
  setLoading: (loading) => () => ({
    loading,
  }),
};

interface Children {
  photos?: Partial<GetPhotosReturnType>;
  loadMore: () => void;
  loading: boolean;
  error: boolean;
}

interface DataProps {
  children: (args: Children) => React.ReactNode;
  shouldLoad: boolean;
}

import { PermissionsAndroid } from 'react-native';

const requestCameraPermission = async () => {
  if (Platform.OS !== 'android') {
    return await Permissions.check('photo');
  }

  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE
    );

    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      return true;
    } else {
      return false;
    }
  } catch (err) {
    return false;
  }
};

const loadPhotos = async ({
  photos,
  setPhotos,
  setError,
  setLoading,
}: Actions & State) => {
  setLoading(true);
  const approved = await requestCameraPermission();

  if (!approved) {
    setLoading(false);
    return setError(true);
  }

  CameraRoll.getPhotos({
    first: 10,
    after: photos!.page_info ? photos!.page_info!.end_cursor : undefined,
    assetType: Platform.OS === 'ios' ? 'All' : 'Photos'
  })
    .then((cameraRoll) => {
      setLoading(false);

      if (photos!.page_info && !photos!.page_info!.has_next_page) {
        return;
      }

      setPhotos({
        ...cameraRoll,
        edges: [...photos!.edges!, ...cameraRoll.edges],
      });
    })
    .catch(() => {
      setLoading(false);
      setError(true);
    });
};

export const Data: React.SFC<DataProps> = ({ children, shouldLoad }) => (
  <Container
    actions={actions}
    initialState={{ photos: { edges: [] }, error: false }}
  >
    {({ photos, setPhotos, setError, error, setLoading, loading }) => (
      <>
        {shouldLoad && (
          <Mount
            on={() =>
              loadPhotos({
                photos,
                setPhotos,
                setError,
                error,
                setLoading,
                loading,
              })
            }
          >
            {null}
          </Mount>
        )}
        {children({
          photos,
          error,
          loadMore: () => {
            loadPhotos({
              photos,
              setPhotos,
              setError,
              error,
              setLoading,
              loading,
            });
          },
          loading,
        })}
      </>
    )}
  </Container>
);
