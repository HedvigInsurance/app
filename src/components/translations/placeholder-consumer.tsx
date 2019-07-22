import * as React from 'react';

import { TranslationsConsumer } from './consumer';

export const TranslationNode: React.SFC = ({ children }) => <>{children}</>;

interface Replacements {
  [key: string]: React.ReactNode;
}

interface TranslationsPlaceholderConsumerProps {
  children: (nodes: React.ReactNode[]) => React.ReactNode;
  textKey: string;
  replacements: Replacements;
}

const placeholderRegex = new RegExp('({[a-zA-Z0-9_]+})', 'g');
const placeholderKeyRegex = new RegExp('([a-zA-Z0-9_]+)', 'g');

export const replacePlaceholders = (
  replacements: Replacements,
  text: string,
) => {
  const matches = text.split(placeholderRegex).filter((value) => value);

  if (!matches) {
    return [];
  }

  return matches.map((placeholder, index) => {
    if (!placeholderKeyRegex.test(placeholder)) return placeholder;
    const key = placeholder.match(placeholderKeyRegex)![0];

    if (replacements[key]) {
      return <TranslationNode key={index}>{replacements[key]}</TranslationNode>;
    }

    return placeholder;
  });
};

export const TranslationsPlaceholderConsumer: React.SFC<
  TranslationsPlaceholderConsumerProps
> = ({ textKey, replacements, children }) => (
  <TranslationsConsumer textKey={textKey}>
    {(text) => children(replacePlaceholders(replacements, text))}
  </TranslationsConsumer>
);
