# CSE 110 Team 7

This is an implementation of SayIt Assistant, a desktop application for answering spoken questions via OpenAI's DaVinci and Whisper APIs.

## Compiling and Running

To compile the source, run:

```
$ make
```

To start the backend server using the included API token, run:

```
$ make server
```

Then, start the frontend in a separate terminal window with:

```
$ make client
```

## Testing and Linting

To run the automated test suite, run:

```
$ make test
```

This project has linting via [checkstyle](https://github.com/checkstyle/checkstyle).  To execute the linter, run:

```
$ make check
```

This is a pre-commit hook, meaning all linting checks must pass before a commit can proceed.

## Team Members

- Andrew Russell
- Tyler Lee
- Adhithya Ananthan
- Pranav Nair
- Geon Kang
