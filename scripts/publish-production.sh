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
./scripts/create-expo-production-config.js

exp login -u $EXPO_USERNAME_PRODUCTION -p $EXPO_PASSWORD_PRODUCTION

exp publish --release-channel production --config ../app.production.json

exp logout