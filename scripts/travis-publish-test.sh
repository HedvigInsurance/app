#!/usr/bin/env bash
if test "$BASH" = "" || "$BASH" -uc 'a=();true "${a[@]}"' 2>/dev/null; then
    # Bash 4.4, Zsh
    set -euo pipefail
    shopt -s nullglob globstar
else
    # Bash 4.3 and older chokes on empty arrays with set -u.
    set -eo pipefail
fi

if [ -z "${TRAVIS_PULL_REQUEST}" ] || [ "${TRAVIS_PULL_REQUEST}" == "false" ]; then
    if [ "${TRAVIS_BRANCH}" == "develop" ]; then
        sudo sysctl fs.inotify.max_user_watches=524288
        sudo sysctl fs.inotify.max_queued_events=524288
        sudo sysctl -p

        ./scripts/publish-test.sh
    else
        echo "Not on develop, will not deploy to test"
    fi
else
    echo "Not on develop, will not deploy to test"
fi
