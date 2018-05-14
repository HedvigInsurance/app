#!/usr/bin/env bash
if test "$BASH" = "" || "$BASH" -uc 'a=();true "${a[@]}"' 2>/dev/null; then
    # Bash 4.4, Zsh
    set -euo pipefail
    shopt -s nullglob globstar
else
    # Bash 4.3 and older chokes on empty arrays with set -u.
    set -eo pipefail
fi

./scripts/create-env-config.js

exp login -u $EXPO_USERNAME_TEST -p $EXPO_PASSWORD_TEST

exp build:ios --release-channel test
exp build:android --release-channel test
