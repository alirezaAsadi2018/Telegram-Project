package com.telegram.utility;

public enum ClientState {
    successfulRegistration, offline, successfulSignIn, failedRegistration, failedSignIn, successfulAddContact, failedAddContact,
    closedSocket, textMessageSent, textMessageFailed
}
