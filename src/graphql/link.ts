import Config from '@hedviginsurance/react-native-config';
import { Alert, Linking } from 'react-native';
import { createUploadLink } from 'apollo-upload-client';
import { setContext } from 'apollo-link-context';
import { onError } from 'apollo-link-error';

import { getToken } from './context';

const showNetworkAlert = () => {
  Alert.alert(
    'Nätverksproblem',
    'Kontrollera att du har anslutning till internet och försök igen.',
    [],
    {
      cancelable: false,
    },
  );
};

const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (graphQLErrors) {
    console.log(graphQLErrors);

    const shouldShowAlert = graphQLErrors.find((error) =>
      error.message.includes('status: 500'),
    );

    if (shouldShowAlert) {
      Navigation.setRoot({
        root: {
          stack: {
            children: [],
          },
        },
      });

      Alert.alert(
        'Kunde inte ladda hedvig 😢',
        'Försök igen senare.',
        [
          {
            text: 'Skicka mail till Hedvig',
            onPress: () =>
              Linking.openURL(
                'mailto:hedvig@hedvig.com?subject=Problem%20att%20ladda%20Hedvig',
              ),
          },
        ],
        {
          cancelable: false,
        },
      );
    }
  }

  if (networkError) {
    try {
      showNetworkAlert();
    } catch (err) {
      Navigation.setRoot({
        root: {
          stack: {
            children: [],
          },
        },
      });

      showNetworkAlert();
    }
  }
});

const uploadLink = createUploadLink({
  uri: Config.GRAPHQL_URL,
});

const setAuthorizationLink = setContext(async () => ({
  headers: { Authorization: await getToken() },
}));

export const link = errorLink.concat(setAuthorizationLink.concat(uploadLink));
