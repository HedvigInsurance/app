import { ApolloClient } from 'apollo-client';
import { InMemoryCache, IntrospectionFragmentMatcher } from 'apollo-cache-inmemory';

import { link } from './link';

import introspectionData from './fragmentTypes';

const fragmentMatcher = new IntrospectionFragmentMatcher({ introspectionQueryResultData: introspectionData })

export const client = new ApolloClient({
  cache: new InMemoryCache({ fragmentMatcher }),
  link,
});
